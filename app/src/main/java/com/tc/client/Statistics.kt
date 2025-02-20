package com.tc.client

import java.util.Collections

object Statistics {

    private var leftSpectrum: MutableList<Double> = Collections.synchronizedList(mutableListOf<Double>());
    private var rightSpectrum: MutableList<Double> = Collections.synchronizedList(mutableListOf<Double>());

    // !!! deprecated !!!
    fun updateSpectrum(ls: List<Double>, rs: List<Double>) {
        if (leftSpectrum.size != ls.size) {
            leftSpectrum.clear();
            leftSpectrum.addAll(ls);
        } else {
            ls.forEachIndexed { idx: Int, value: Double ->
                leftSpectrum[idx] = value
            };
        }

        if (rightSpectrum.size != rs.size) {
            rightSpectrum.clear();
            rightSpectrum.addAll(rs);
        } else {
            rs.forEachIndexed { idx: Int, value: Double ->
                rightSpectrum[idx] = value
            }
        }
    }

}