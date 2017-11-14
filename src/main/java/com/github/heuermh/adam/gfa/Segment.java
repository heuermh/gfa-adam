/*

    gfa-adam  Graphical Fragment Assembly (GFA) 2.0 support for ADAM.
    Copyright (c) 2017 held jointly by the individual authors.

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.fsf.org/licensing/licenses/lgpl.html
    > http://www.opensource.org/licenses/lgpl-license.php

*/
package com.github.heuermh.adam.gfa;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableMap;

@Immutable
public final class Segment extends GfaRecord {
    private final String id;
    private final Integer length;
    private final String sequence;
    private final Map<String, Tag> tags;

    public Segment(@Nullable final String id,
                   @Nullable final Integer length,
                   @Nullable final String sequence,
                   final Map<String, Tag> tags) {

        checkNotNull(tags);

        this.id = id;
        this.length = length;
        this.sequence = sequence;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public Integer getLength() {
        return length;
    }

    public String getSequence() {
        return sequence;
    }

    public Map<String, Tag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "S", id, length, sequence == null ? "*" : sequence);
        if (!tags.isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, tags.values());
        }
        return sb.toString();
    }

    public static Segment valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("S"), "value must start with S");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 4) {
            throw new IllegalArgumentException("value must have at least four tokens, was " + tokens.size());
        }

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 4; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getTag(), tag);
        }

        return new Segment(tokens.get(1), Integer.parseInt(tokens.get(2)), tokens.get(3), tags.build());
    }
}
