package org.omgwtfbbq.util

import static org.omgwtfbbq.util.LogUtil.debug

import groovy.util.logging.Commons

/*
	Methods for procuring an option value from a Map 'options'.  There are
	different possibilities for verbose logging,  requiring an option be
	present, and providing defaults in case an option isn't present.
*/

@Commons
abstract class Option {

	static getOption(options, name, String blurb = null) { return getOption(options, name, [blurb: blurb]) }

	// 'P' for print
	static getOptionP(options, name) { getOption(options, name, [output: true]) }
	// 'D'efault && 'P'rint
	static getOptionDP(options, name, defaultValue) { getOption(options, name, [output: true, 'default': defaultValue]) }
	static getOptionD(options, name, defaultValue) { getOption(options, name, ['default': defaultValue]) }
	// 'OO' -- optional option
	static getOOptionP(options, name) { getOption(options, name, [optional: true]) }

	// oo == option options
	static getOption(options, name, Map oo) {
		if (!options.containsKey(name)) {
			if (oo.optional) {
				if (oo.output) debug("Option '$name' not present but not required.")
				return null
			}

			if (oo.containsKey('default')) {
				if (oo.output) {
					debug("Option '$name' has default value ${oo.default}")
				}
				return oo.default
			}

			if (oo.blurb) {
				throw new RuntimeException(oo.blurb)
			} else {
				throw new RuntimeException("Need $name")
			}
		}

		if (oo.output) {
			debug("Option '$name' has value ${options[name]}")
		}
		return options[name]
	}

	private Option() {}
}

