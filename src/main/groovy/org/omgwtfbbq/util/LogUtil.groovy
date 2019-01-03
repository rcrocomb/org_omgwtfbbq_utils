#!/usr/bin/env groovy

package com.shutterfly.lti.util

import groovy.util.logging.Commons
import org.apache.commons.logging.Log

@Commons
abstract class LogUtil {

	// 'p' is for 'prefix': it's what you put out first when using the hacky
	// debug/info/warn/error methods.
	private static final p = new ThreadLocal<String>() { @Override protected synchronized String initialValue() { "" } }

	static String sp(String s) { p.set(s); return p.get() }		// set p
	static String spp(String s) { p.set(p.get() + s); return p.get() }	// set p plus -- p += s
	static String gp() { p.get() }						// get p
	static String clrp() { sp("") }						// clear p

////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////

	static void debug(Log l, Map m) {
		debug(l, "" + m)
	}

	static void debug(Map m) {
		debug(log, m)
	}

	static void debug(Log l, List list) {
		debug(l, list)
	}

	static void debug(List l) {
		debug(log, "size ${l.size()} --> " + l)
	}

	static void debug(boolean quiet, String s) {
		if (!quiet) debug(s)
	}

	static void debug(Log l, String s) {
		l.debug(gp() + " " + s);
	}

	static void debug(String s) {
		debug(log, s)
	}

	static void debug(boolean quiet, String format, Object... args) {
		if (!quiet) debug(format, args)
	}

	static void debug(String format, Object... args) {
		debug(log, format, args)
	}

	static void debug(Log l, String format, Object... args) {
		try {
			l.debug(String.format(gp() + " " + format, args));
		} catch (IllegalFormatConversionException e) {
			l.error("For format string >>${format}<< the error is '${e.message}'")
			whatIsBad(format, args)
		}
	}

	static void info(String s) {
		log.info(gp() + " " + s);
	}

	static void info(String format, Object... args) {
		log.info(String.format(gp() + " " + format, args));
	}

	static void warn(String s) {
		log.warn(gp() + " " + s);
	}

	static void warn(String format, Object... args) {
		log.warn(String.format(gp() + " " + format, args));
	}

	static void error(String s) {
		log.error(gp() + " " + s);
	}

	static void error(String format, Object... args) {
		log.error(String.format(gp() + " " + format, args));
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

	/*
		Don't use debug() to avoid recursing.

		Okay, the Groovy Exception for bad format strings is freakin' garbage and I'm tired of it.

		I got

Caught: java.util.IllegalFormatConversionException: d != java.lang.String
java.util.IllegalFormatConversionException: d != java.lang.String
	at java_lang_String$format$1.call(Unknown Source)
	at C.debug(hacks.groovy:16520)
	at C$_getProcessInstanceStatus_closure23$_closure219.doCall(hacks.groovy:1820)
	at C$_getProcessInstanceStatus_closure23.doCall(hacks.groovy:1816)
	at C.getProcessInstanceStatus(hacks.groovy:1746)
	at C$_deleteProcessInstances_closure106.doCall(hacks.groovy:8856)
	at C.deleteProcessInstances(hacks.groovy:8855)
	at C$deleteProcessInstances$5.callCurrent(Unknown Source)
	at C.go(hacks.groovy:17134)
	at C$go.call(Unknown Source)
	at C.main(hacks.groovy:17146)

	On this format String:
		debug("[%2d/%2d] %2d -- %1d -- %1s -- %8d -- %-20s", ...)
	That's not a lot of help.  Yeah, I guess we know it's one of the first 4,
	but we HAVE all the damn data.  Go get it, damn it.

	I wrote my own little format string parser jobby, figuring it'd be cleaner than
	having to build in some Apache Commons monstrosity (I didn't even look, though).
	It's a skosh bigger than I'd hoped...  Ah well.  I like state machines!
	*/

	private static void whatIsBad(format, Object... variables) {
		def specifiers = getFormatSpecifiers(format)
		specifiers.eachWithIndex { k, v, i ->
			try {
				// Due to LinkedHashMap, we can rely on order
				String.format(v, variables[i])
			} catch (IllegalFormatConversionException e) {
				// AWESOME. We can output variables[i] using '%s' no matter what
				// so we can describe our illegal conversion using it.  hee hee
				log.debug(String.format(
					"Okay, the specifier @ index %d starting at position %d '%s' ... has an illegal conversion '%s' -- type of variable is '%s' --> '%s'",
					i, k, v, e.message, variables[i].getClass().simpleName, variables[i]))
			}
		}
	}

	// SpecifierState
	enum SS {
		MAYBE_IN_SPECIFIER,	// or could be literal %
		IN_SPECIFIER,		// definitely in specifier
		NOT_IN_SPECIFIER	// definitely not in specifier
	}

	/*
		DON'T USE debug() or info() because we don't want to recurse into this method
		DON'T USE '$bleh' here because a lot of stuff has '%'

		Whelp, that got out of hand fast.

		Map is from starting location in format String to the specifier found starting at that location

		This parser is definitely not 100% correct.  I probably should
		have just looked to see if the Groovy/Java ones were
		available.  Oh well.
	*/

	private static Map<Integer, String> getFormatSpecifiers(String format) {
		def specifiers = [:]
		def non_specifiers = [:]
		int start = -1
		SS state = SS.NOT_IN_SPECIFIER
		format.eachWithIndex { c, i ->
			String s = String.format("[%2d/%2d]", i, format.size())
//			log.debug(String.format("%s .. '%s'", s, c))

			switch(state) {
			case SS.NOT_IN_SPECIFIER:
				if (c == '%') {
//					log.debug(String.format("%s Okay, percent sign.  Could be literal percent.", s))
					state = SS.MAYBE_IN_SPECIFIER
					start = start == -1 ? i : start
				} else {
					// continuing to not be in specifier
					start = start == -1 ? i : start
//					log.debug(String.format("%s .. still not.   So far '%s'", s, format[start..i]))
				}
				break
			case SS.MAYBE_IN_SPECIFIER:
				if (c == '%') {
//					log.debug(String.format("%s Looks like literal percent sign due to '%s'", s, format[i - 1..i]))
					state = SS.NOT_IN_SPECIFIER	// literal '%'
					// Don't reset start: continue growing the 'not specifier' size
				} else {
//					log.debug(String.format("%s looks like we're entering a specifier", s))
					state = SS.IN_SPECIFIER

					// How to know if it's legit vs. we started a string w/ a format specifier
					if (start != -1) {
						// If we went from "maybe" to yes then both format[i] and format[i]
						// are definitely *not* part of the non-specifier, so...
						String x = format[start..i-2]
						log.debug(String.format("%s non-specifier substring '%s'", s, x))
						non_specifiers[start] = x
					}
					start = i - 1
					// I want to leave start unchanged until now in case we didn't ever become a specifier
				}
				break
			case SS.IN_SPECIFIER:
				if (c ==~ /[\]\/% 	]/) {
					String format_specifier = format[start..i-1]
					specifiers[start] = format_specifier
//					log.debug(String.format("%s leaving format specifier on '%s' --> produced format specifier '%s' on substring from %d --> %d ", s, c, format_specifier, start, i-1))
//					log.debug(String.format("%s There are %d total format specifiers now --> %s", s, specifiers.size(), specifiers))
					// leaving specifier, possibly for another
					if (c == '%') {
//						log.debug(String.format("%s from one specifier to another?  New char is '%s'", s, c))
						state = SS.MAYBE_IN_SPECIFIER
						start = i
					} else {
//						log.debug(String.format("%s from specifier to non-specifier due to '%s'", s, c))
						// don't use debug() so we don't recurse into this method
						state = SS.NOT_IN_SPECIFIER
						start = i // beginning of non_specifier string
					}
				} else {
//					log.debug(String.format("%s Still in specifier.. so far '%s'", s, format[start..i]))
				} // no transition IN_SPECIFIER --> MAYBE_IN_SPECIFIER, only "stay in" or "get out"
				break
			default:
				throw new RuntimeException("WTF")
			}
		}

		if (state == SS.IN_SPECIFIER) {
			String format_specifier = format[start..-1]
			log.debug(String.format("%3d leaving format specifier on hitting end of string --> produced format specifier '%s' on substring from %d --> %d", format.size(), format_specifier, start, format.size()))
			specifiers[start] = format_specifier
		} else {
			String x = format[start..-1]
			log.debug(String.format("%3d Ended string w/ non-specifier '%s' from %d --> %d", format.size(), x, start, format.size()))
			non_specifiers[start] = x
		}

		log.debug(String.format("We out -- found %d specifiers -- %s", specifiers.size(), specifiers))
		log.debug(String.format("We out -- found %d non-specifiers -- %s", specifiers.size(), non_specifiers))
		return specifiers
	}

	private LogUtil() {}
}

