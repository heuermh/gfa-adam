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

import java.util.stream.Collectors;

import javax.annotation.Nullable;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Immutable
public final class Path extends GfaRecord {
    private final String id;
    private final List<Reference> references;
    private final Map<String, Tag> tags;

    public Path(@Nullable final String id,
                final List<Reference> references,
                final Map<String, Tag> tags) {

        checkNotNull(references);
        checkNotNull(tags);

        this.id = id;
        this.references = ImmutableList.copyOf(references);
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public Map<String, Tag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        Joiner joiner = Joiner.on("\t");
        StringBuilder sb = new StringBuilder();
        joiner.appendTo(sb, "O", id == null ? "*" : id, Joiner.on(" ").join(references));
        if (!tags.isEmpty()) {
            sb.append("\t");
            joiner.appendTo(sb, tags.values());
        }
        return sb.toString();
    }

    public static Path valueOf(final String value) {
        checkNotNull(value);
        checkArgument(value.startsWith("O"), "value must start with O");
        List<String> tokens = Splitter.on("\t").splitToList(value);
        if (tokens.size() < 9) {
            throw new IllegalArgumentException("value must have at least nine tokens, was " + tokens.size());
        }
        String id = "*".equals(tokens.get(1)) ? null : tokens.get(1);
        List<Reference> references = Splitter
            .on(" ")
            .splitToList(tokens.get(2))
            .stream()
            .map(v -> Reference.valueOf(v))
            .collect(Collectors.toList());

        ImmutableMap.Builder<String, Tag> tags = ImmutableMap.builder();
        for (int i = 8; i < tokens.size(); i++) {
            Tag tag = Tag.valueOf(tokens.get(i));
            tags.put(tag.getTag(), tag);
        }

        return new Path(id, references, tags.build());
    }
}
