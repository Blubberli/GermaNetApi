/*
 * Copyright (C) 2012 Department of General and Computational Linguistics,
 * University of Tuebingen
 *
 * This file is part of the Java API to GermaNet.
 *
 * The Java API to GermaNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Java API to GermaNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this API; if not, see <http://www.gnu.org/licenses/>.
 */
package de.tuebingen.uni.sfs.germanet.api;

import java.util.Set;

/**
 * Simple class to store LeastCommonSubsumer information. Getters are available for all fields.
 *
 * @author Marie Hinrichs, Seminar für Sprachwissenschaft, Universität Tübingen
 */
public class LeastCommonSubsumer {
    private int lcsID;
    private Set<Integer> fromToSynsetIDs;
    private int distance;

    public LeastCommonSubsumer(int lcsID, Set<Integer> fromToSynsetIDs, int distance) {
        this.lcsID = lcsID;
        this.fromToSynsetIDs = fromToSynsetIDs;
        this.distance = distance;
    }

    /**
     * Get the Synset ID of the least common subsumer.
     * @return the Synset ID of the least common subsumer
     */
    public int getLcsID() {
        return lcsID;
    }

    /**
     * Get the Synset IDs of the two synsets.
     * @return the Synset IDs of the two synsets
     */
    public Set<Integer> getFromToSynsetIDs() {
        return fromToSynsetIDs;
    }

    /**
     * Get the shortest distance between the two synsets using hypernym / hyponym relations.
     * @return the shortest distance between the two synsets using hypernym / hyponym relations
     */
    public int getDistance() {
        return distance;
    }

    public String toString() {
        return  "\nlcsID: " + lcsID + " fromTo: " + fromToSynsetIDs + " distance: " + distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeastCommonSubsumer that = (LeastCommonSubsumer) o;

        if (lcsID != that.lcsID) return false;
        if (distance != that.distance) return false;
        return fromToSynsetIDs.equals(that.fromToSynsetIDs);
    }

    @Override
    public int hashCode() {
        int result = lcsID;
        result = 31 * result + fromToSynsetIDs.hashCode();
        result = 31 * result + distance;
        return result;
    }
}
