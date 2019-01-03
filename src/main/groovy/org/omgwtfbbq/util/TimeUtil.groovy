package org.omgwtfbbq.util

import groovy.util.logging.Commons

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Commons
abstract class TimeUtil {

	public static final ZoneId UTC_ZONE = ZoneId.of("UTC");

	/*
		I once wrote code to test System.currentTimeMillis() vs.
		System.nanoTime() to see if there was a difference in how fast
		it was to call, and on a few different x86 processors the
		answer was "no".  Since I'd rather have a meaningful absolute
		number as opposed to just meaningful relative numbers once
		you've computed a difference, I'm sticking with millis.
	*/

	static timeIt(closure) {
		long before = System.currentTimeMillis()
		def result = closure()
		long after = System.currentTimeMillis()
		long diff = after - before
		return [result: result, diff: diff, before: before, after: after]
	}

	/*
		A List of the data values in 'result, diff, before, after'
		order.
	*/

	static timeIt2(closure) {
		return timeIt(closure).values()
	}

	/*
		Take a string representing a date and time, 'dateString', which
		is formatted like 'format' and considered to be in UTC and convert
		it to the timezone 'destTimeZone'.  Return that ZonedDateTime.

		So presumably 'format' should not have 'z', 'Z', 'X', etc. ?
	*/

	static ZonedDateTime moveUTCtoZone(String dateString, String format, String destTimeZone) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
		return LocalDateTime.parse(dateString, formatter) \
			.atZone(UTC_ZONE)
			.withZoneSameInstant(ZoneId.of(destTimeZone))
	}

	/*
		Take a string representing a date and time, 'dateString', which
		is formatted like 'format', and convert it into a ZonedDateTime
		in the timezone 'destTimeZone'.  Return that ZonedDateTime.
	*/

	static ZonedDateTime moveZone(String dateString, String format, String destTimeZone) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format)
		return ZonedDateTime.parse(dateString, formatter) \
			.withZoneSameInstant(ZoneId.of(destTimeZone))
	}

	static String format(ZonedDateTime dateTime, String format) {
		return dateTime.format(DateTimeFormatter.ofPattern(format))
	}

	static String now(String formatS) {
		format(Instant.now().atZone(UTC_ZONE), formatS)
	}

	private TimeUtil() {}
}

