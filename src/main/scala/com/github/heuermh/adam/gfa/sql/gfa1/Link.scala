/*

    adam-gfa  Graphical Fragment Assembly (GFA) support for ADAM.
    Copyright (c) 2017-2021 held jointly by the individual authors.

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
package com.github.heuermh.adam.gfa.sql.gfa1

import org.dishevelled.bio.assembly.gfa1.{ Link => JLink }

import scala.collection.JavaConverters._

/**
 * GFA 1.0 link case class for use in data frames.
 */
case class Link(
  id: String,
  source: Reference,
  target: Reference,
  overlap: String,
  mappingQuality: Option[Int],
  mismatchCount: Option[Int],
  readCount: Option[Int],
  fragmentCount: Option[Int],
  kmerCount: Option[Int],
  annotations: Map[String, Annotation]) {

  def asJava(): JLink = {
    new JLink(
      source.asJava,
      target.asJava,
      overlap,
      annotations.map(kv => (kv._1, kv._2.asJava)).asJava
    )
  }
}

object Link {
  def apply(l: JLink): Link = {
    Link(
      id = l.getIdOpt.orElse(null),
      source = Reference(l.getSource),
      target = Reference(l.getTarget),
      overlap = l.getOverlap,
      mappingQuality = if (l.containsMappingQuality) Some(l.getMappingQuality) else None,
      mismatchCount = if (l.containsMismatchCount) Some(l.getMismatchCount) else None,
      readCount = if (l.containsReadCount) Some(l.getReadCount) else None,
      fragmentCount = if (l.containsFragmentCount) Some(l.getFragmentCount) else None,
      kmerCount = if (l.containsKmerCount) Some(l.getKmerCount) else None,
      annotations = l.getAnnotations.asScala.map(kv => (kv._1, Annotation(kv._2))).toMap
    )
  }

  def apply(r: Gfa1Record): Link = {
    Link(
      r.id,
      r.source,
      r.target,
      r.overlap,
      r.mappingQuality,
      r.mismatchCount,
      r.readCount,
      r.fragmentCount,
      r.kmerCount,
      r.annotations
    )
  }
}
