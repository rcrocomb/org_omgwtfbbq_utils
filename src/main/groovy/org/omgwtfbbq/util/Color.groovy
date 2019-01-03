package org.omgwtfbbq.util

import groovy.util.logging.Commons

@Commons
abstract class Color {

	////////////////////////////////////////////////////////////////////////////////
	// Fancy terminal stuff
	////////////////////////////////////////////////////////////////////////////////

	static final String BOLD       = "01"
	static final String DIM        = "02"
	static final String UNDERLINED = "04"
	static final String BLINK      = "05"
	static final String INVERT     = "07"
	static final String HIDDEN     = "08"

	// Text is pretty dark without 'BOLD'
	// I think that 'LIGHT' loses when text is BOLD: bold green looks identical to light green to me
	// They definitely are different when they aren't BOLD

	static final String RED         = "\033[$BOLD;$BLINK;31m"
	static final String GREEN       = "\033[$BOLD;32m"
	static final String YELLOW      = "\033[$BOLD;33m"
	static final String BLUE        = "\033[$BOLD;34m"
	static final String MAGENTA     = "\033[$BOLD;35m"
	static final String CYAN        = "\033[$BOLD;36m"
	static final String LIGHT_GRAY  = "\033[$BOLD;37m"

	static final String DARK_GRAY     = "\033[$BOLD;90m"
	static final String LIGHT_RED     = "\033[$BOLD;91m"
	static final String LIGHT_GREEN   = "\033[$BOLD;92m"
	static final String LIGHT_YELLOW  = "\033[$BOLD;93m"
	static final String LIGHT_BLUE    = "\033[$BOLD;94m"
	static final String LIGHT_MAGENTA = "\033[$BOLD;95m"
	static final String LIGHT_CYAN    = "\033[$BOLD;96m"
	static final String WHITE         = "\033[$BOLD;97m"

	// There are other things that only reset bold/dim/underlined/blink/reverse/hidden
	static final String STOP = '\033[00m'

////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////

	static void color(colorname, text)                { color("", colorname, text, "") }
	static void color(colorname, before, text)        { color(before, colorname, text, "") }
	static void color(colorname, before, text, after) { doit(before, colorname, text, STOP, after) }

	static void blue(text)                { color(BLUE, text) }
	static void blue(before, text)        { color(before, BLUE, text) }
	static void blue(before, text, after) { color(BLUE, before, text, after) }

	static void magenta(text)                { color(MAGENTA, text) }
	static void magenta(before, text)        { color(before, MAGENTA, text) }
	static void magenta(before, text, after) { color(MAGENTA, before, text, after) }

	static void cyan(text)                { color(CYAN, text) }
	static void cyan(before, text)        { color(before, CYAN, text) }
	static void cyan(before, text, after) { color(CYAN, before, text, after) }

	static void red(red)                { doit("", RED, red, STOP, "") }
	static void red(before, red)        { doit(before, RED, red, STOP, "") }
	static void red(before, red, after) { doit(before, RED, red, STOP, after) }

	static void green(green)                { doit("", GREEN, green, STOP, "") }
	static void green(before, green)        { doit(before, GREEN, green, STOP, "") }
	static void green(before, green, after) { doit(before, GREEN, green, STOP, after) }

	static void yellow(yellow)                { doit("", YELLOW, yellow, STOP, "") }
	static void yellow(before, yellow)        { doit(before, YELLOW, yellow, STOP, "") }
	static void yellow(before, yellow, after) { doit(before, YELLOW, yellow, STOP, after) }

	// TODO: flavor that just returns text.  Maybe I want to use log.warn not log.info, etc.

	private static void doit(before, turnon, during, turnoff, after) {
		String s = ""
		if (before) {
			s = "$before "
		}
		if (during) {
			s += turnon + during + turnoff
		}
		if (after) {
			if (during) {
				s += " $after"	// expected to be the case
			} else if (before) {
				s += after	// don't need ' '
			} else {
				s += after	// don't need ' '
			}
		}
		log.info(s)
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

/*
	Show examples of various color text.
*/

	static void angryFruitSalad(options) {
		red("I am red")
		green("I am green")
		yellow("I am yellow")
		blue("I am blue")
		magenta("I am magenta")
		cyan("I am cyan")

		color(LIGHT_GRAY, "I am light gray")
		color(DARK_GRAY, "I am dark gray")
		color(LIGHT_RED, "I am light red")
		color(LIGHT_GREEN, "I am light green")
		color(LIGHT_YELLOW, "I am light yellow")
		color(LIGHT_BLUE, "I am light blue")
		color(LIGHT_MAGENTA, "I am light magenta")
		color(LIGHT_CYAN, "I am light cyan")
		color(WHITE, "I am white")
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

	private LogUtil() {}
}

