package org.omgwtfbbq.util

import com.google.gson.Gson
import groovy.util.logging.Commons

import java.security.MessageDigest
import java.util.concurrent.TimeUnit

import static org.omgwtfbbq.util.LogUtil.debug

@Commons
abstract class Util {

	// ISO-8601 DateFormat string
	public static final String FORMAT_STRING = "yyyy-MM-dd HH:mm:ss"
	public static final Double BYTES_TO_MB = 1000.0 * 1000.0

	static double B2MB(long bytes) { return (bytes * 1.0) / BYTES_TO_MB }

	// From a Map-like thing to a JSON string
	static o2Json(someObject) { new Gson().toJson(someObject) }
	// From a String that is JSON to a Map-like thing
	static s2Json(String s) { s ? new Gson().fromJson(s) : null }

////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////

	/*
		Output the freakin' value that didn't work
	*/

	long gimmeLong(String s) {
		try {
			return s.toLong()
		} catch (NumberFormatException e) {
			throw new RuntimeException("Failed to convert '$s' to a Long")
		}
	}

	Long gimmeLong(description, stringValue) {
		if (!stringValue) {
			return null
		}

		try {
			return Long.valueOf(stringValue)
		} catch (NumberFormatException ignored) {
			println "Non-numerical '$description' --> $stringValue"
			return null
		} catch (Exception e) {
			println "Unexpected exception for '$decription' -- run away -- ${e}"
			throw e
		}
	}

	static String shaString(options) {
		String someString = options.filenames[0]
		MessageDigest digest = MessageDigest.getInstance("SHA-256")
		String hash = digest.digest(someString.bytes).encodeHex().toString()
//		log.debug("Sha-ing '$someString' --> $hash")
		return hash
	}


	static long bucketize(Long value, Long bucketSize) {
		Long bucketCounts = value / bucketSize // truncate
		return bucketCounts * bucketSize
	}

	static long bucketizeToS(Long uSecValue, Long seconds) {
		return bucketize(uSecValue, TimeUnit.SECONDS.toMicros(seconds))
	}

////////////////////////////////////////////////////////////////////////////////
//
////////////////////////////////////////////////////////////////////////////////

	/*
		No "bursting" support or other crap, but at least it can damn well
		process a set of command line arguments with unknown values without
		blowing the hell up.  CliBuilder == garbage (or at least its underlying
		parser).

		Supports arguments of the form:

		 -optionName value (single dash)
		--optionName value (double dash)

		It is not permitted for value to start with a dash.

		For booleans

		--optionName

		will result in options[optionName] == true and

		--no-optionName

		will result in options[optionName] == false

		Additionally for booleans you can do

		--optionName true

		or

		--optionName false

		We consider all strings that don't start with '-' or '--' that aren't
		option values to potentially be filenames. These strings are confirmed
		to be filenames if they continue to the end of the option arguments,
		i.e.
			--option value filename1
		there is one filename, but
			--option value filename1 --option2 value
		there are no filenames here: 'filename1' is considered to be an
		unassociated option argument and therefore in error. There can be
		multiple filenames:
			--option value filename1 filename2 ... filename[n]

		Just like with rm, you can use '--' to terminate option processing: any
		remaining arguments will be treated as filenames.  Any in-progress
		option, e.g. here with 'x':

			--x -- filename 1 filename 2

		will be treated as boolean.  You must not supply an option name starting
		with '-', e.g.

			-x -- --bad dont

		'--bad' will cause an exception.  Aww damn it, The Groovy commandline
		eats '--' and won't even pass it into a script's main(), e.g.:

		./dvd.groovy --x --y -- filename

		For 'public static main(String[] args) { println args }'

		You will get [--x, --y, filename].  I haven't figured out any combination
		of escapes or quoting that will result in you actually getting the full
		commandline.  I guess use '==' instead.  Real nice.

		'args' is expected to be 'String[]' or equivalent.

		Returns a map of the commandline options with the option name as the key
		and the option value a the map value.
	 */

	static Map parse(args, parseOptions = null) {
		boolean q = parseOptions?.quiet ?: false	// be quiet, true or false?
		def options = [:]
		String optionName = null
		boolean forceOptionEnd = false
		for (int i = 0; i < args.size(); ++i) {
			String value = args[i]
			debug(q, "Parsing value [$i] -> '$value'")

			// option values may not start with '-'
			if (value.startsWith("-") || value.startsWith('=')) {
				debug(q, "Looks like option name '$value'")
				// should be an option name

				if (forceOptionEnd) {
					log.error("Found option name '$value' after explicitly end options via '--/=='.  Error.")
					throw new RuntimeException("At position '$i' found an option name '$value' after explicitly forcing end of options via '--/=='.  Error")
				}

				// See if we're already doing an option name: we'll assume that
				// means previous option is a boolean.  Similarly for an
				// argument for which there can't be a value because we're out
				// of 'args' elements.
				if (optionName) {
					debug(q, "optionName already set to '$optionName': assuming it's a boolean")
					// If option start with 'no-', we want to set boolean value
					// to false
					if (optionName.startsWith('no-')) {
						debug(q, "Setting boolean to false: ${optionName[3..-1]}")
						// take the 'no-' off
						options[optionName[3..-1]] = false
					} else {
						// set it to true
						debug(q, "Setting boolean to true: ${optionName}")
						options[optionName] = true
					}
				} else if (args.size() == (i + 1)) {
					// single boolean argument?
					debug(q, "I think it's a boolean argument -- let it ride")
				}

				// Value is '--': this means "no more options follow, only filenames"
				if ((value.count("-") == 2 && value.size() == 2) || (value.count('=') == 2 && value.size() == 2)) {
					optionName = null
					forceOptionEnd = true
					debug(q, "Presence of '--' or '++' forces end of options")
					continue
				}

				if (value.startsWith('=')) {
					log.error("Option starting with '=' and is not the force option end '==' value.")
				}

				// strip leading dashes -- replaceAll wouldn't allow for options
				// with dashes in their name.
				int dashCount = 0;
				for (; value.charAt(dashCount) == '-'; ++dashCount);
				optionName = value[dashCount..-1]

				// strip trailing '='
				// TODO:, uhh, better hope we don't get unsplit '--blah=bleh'
				if (optionName.endsWith('=')) {
					optionName = optionName[0..-2]
				}
				debug(q, "Got final option name as '$optionName'")

			} else if (!optionName) {
				// Can only be considered a list of filenames if each string
				// from 'i' to the end does not start with '-'.  Otherwise we
				// consider 'value' to be a non-option mixed in with value
				// options and an error.

				boolean filenameList = true
				def filenames = []
				for (int j = i ; filenameList && j < args.size(); ++j) {
					filenameList = !args[j].startsWith('-')
					filenames << args[j]
				}

				if (filenameList) {
					debug(q, "Got list of filenames from arguments as '$filenames'")
					options.filenames = filenames
					// We've handled all the remaining args to get this far
					break
				} else {
					debug(q,"Looks like we got an option value without previously haven gotten an option name: '$value': unsupported")
					// Something like ./blah whatever -- we don't support this currently
					throw new RuntimeException("@ position '$i' no option name for value '$value'")
				}
			} else if (value == "true") {
				String originalOption = optionName
				boolean sense = true
				if (optionName.startsWith("no-")) {
					sense = false
					optionName = optionName[3..-1] // strip "no-"
				}
				debug(q, "Setting boolean to $sense: ${originalOption}")
				options[optionName] = sense
				optionName = (String)null
			} else if (value == "false") {
				String originalOption = optionName
				// no-thing false --> thing == true
				boolean sense = false
				if (optionName.startsWith("no-")) {
					sense = true
					optionName = optionName[3..-1] // strip "no-"
				}
				debug(q, "Setting boolean to $sense: ${originalOption}")
				options[optionName] = sense
				optionName = (String)null
			} else {
				debug(q, "Option value: setting '$optionName' to '$value'")
				// We now have pair, optionName and the value
				options[optionName] = value
				// Is this bad inside a closure?
				optionName = (String)null
			}
		}

		// Possibly last option was boolean: handle it again
		if (optionName) {
			debug(q, "Looks like optionName after args runs out: handling like usual")
			// If option start with 'no-', we want to see boolean value
			// to false
			if (optionName.startsWith('no-')) {
				// take the 'no-' off
				options[optionName[3..-1]] = false
				debug(q, "Setting boolean to false: ${optionName[3..-1]}")
			} else {
				// set it to true
				options[optionName] = true
			}
		}

		debug(q, "Options map is now '$options'")
		return options
	}

	private Util() {}
}

