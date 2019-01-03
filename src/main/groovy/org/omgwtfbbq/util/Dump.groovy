package org.omgwtfbbq.util

import static org.omgwtfbbq.util.LogUtil.debug

import com.google.gson.Gson

import groovy.util.logging.Commons

import org.apache.commons.logging.Log

@Commons
abstract class Dump {

	// This came from Java, I think?

	static void dump(Log l, Map<?, ?> m, String blurb) {
		if (m == null) {
			if (blurb) debug(l, "Map '$blurb' is null")
			else debug(l, "Null map")
			return
		} else if (blurb) {
			debug(l, "Found $blurb")
		}
		debug(l, "Map of size ${m.size()}")
		int ndigits = m.size().toString().length()
		m.keySet().sort().eachWithIndex { k, i ->
			String v = m[k]
			debug(l, "[%${ndigits}d/%${ndigits}d] %25s --> %s", i, m.size(), k, v)
		}
	}

	static void dump(Map<?, ?> m, String blurb) {
		dump(log, m, blurb)
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

	static void dump(Log l, Map<?, ?> m) {
		dump(l, m, null)
	}

	static void dump(Map<?, ?> m) {
		dump(log, m, null)
	}

	static void dump(Set s) {
		dump(log, s)
	}

	static void dump(Log l, Set s) {
		int ndigits = s.size().toString().length()
		s.sort().eachWithIndex {  it, i -> debug(l, "[%${ndigits}d/%${ndigits}d] %s", i, s.size(), it) }
	}

	static void dump(AttributeValue v) {
		debug(v.s)
	}

	/*
		Fallback to just turning stuff into pretty-printed JSON.  I
		think this will work well with any Amazon model classes
	*/

	static void dump(Log l, Object o) {
		prettyJson(l, Util.o2Json(o))
	}

	static void dump(Object o) {
		prettyJson(o2Json(o))
	}

	// '#' is not in base-64
	private static String forSecret(String secret) { secret[0..3] + ('#' * (secret.length() - 4)) }

	static void prettyJson(json) { prettyJson(log, json) }
	static void prettyJson(Log l, json) { prettyJson(l, o2Json(json)) }

	static void prettyJson(Log l, String s) { debug(l, JsonOutput.prettyPrint(s)) }
	static void prettyJson(String s) { prettyJson(log, s) }

	static String gimmePrettyJson(json) { JsonOutput.prettyPrint(o2Json(json)) }

	private Dump() {}
}
