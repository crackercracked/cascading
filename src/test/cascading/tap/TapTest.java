/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Cascading is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cascading is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cascading.  If not, see <http://www.gnu.org/licenses/>.
 */

package cascading.tap;

import java.io.IOException;
import java.io.Serializable;

import cascading.PlatformTestCase;
import cascading.flow.Flow;
import cascading.operation.regex.RegexSplitter;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.test.PlatformTest;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryIterator;

import static data.InputData.*;

/**
 *
 */
@PlatformTest(platforms = {"local", "hadoop"})
public class TapTest extends PlatformTestCase implements Serializable
  {
  public TapTest()
    {
    super( true );
    }

  public void testSinkDeclaredFields() throws IOException
    {
    getPlatform().copyFromLocal( inputFileCross );

    Tap source = getPlatform().getTextFile( new Fields( "line" ), inputFileCross );

    Pipe pipe = new Pipe( "test" );

    pipe = new Each( pipe, new RegexSplitter( new Fields( "first", "second", "third" ), "\\s" ), Fields.ALL );

    Tap sink = getPlatform().getTextFile( new Fields( "line" ), new Fields( "second", "first", "third" ), getOutputPath( "declaredsinks" ), SinkMode.REPLACE );

    Flow flow = getPlatform().getFlowConnector().connect( source, sink, pipe );

    flow.complete();

    validateLength( flow, 37, null );

    TupleEntryIterator iterator = flow.openSink();

    String line = iterator.next().getString( 0 );
    assertTrue( "not equal: wrong values", line.matches( "[a-z]\t[0-9]\t[A-Z]" ) );

    iterator.close();
    }

  public void testSinkUnknown() throws IOException
    {
    getPlatform().copyFromLocal( inputFileCross );

    Tap source = getPlatform().getTextFile( new Fields( "line" ), inputFileCross );

    Pipe pipe = new Pipe( "test" );

    pipe = new Each( pipe, new RegexSplitter( new Fields( "first", "second", "third" ), "\\s" ), Fields.RESULTS );

    Tap sink = getPlatform().getDelimitedFile( Fields.UNKNOWN, getOutputPath( "unknownsinks" ), SinkMode.REPLACE );

    Flow flow = getPlatform().getFlowConnector().connect( source, sink, pipe );

    flow.complete();

    validateLength( flow, 37, null );

    TupleEntryIterator iterator = flow.openSink();

    String line = iterator.next().getTuple().toString();
    assertTrue( "not equal: wrong values: " + line, line.matches( "[0-9]\t[a-z]\t[A-Z]" ) );

    iterator.close();
    }

  public void testMultiSinkTap() throws IOException
    {
    getPlatform().copyFromLocal( inputFileJoined );

    Tap source = getPlatform().getTextFile( new Fields( "line" ), inputFileJoined );

    Pipe pipe = new Pipe( "test" );

    pipe = new Each( pipe, new RegexSplitter( new Fields( "number", "lower", "upper" ), "\t" ) );

    Tap lhsSink = getPlatform().getTextFile( new Fields( "offset", "line" ), new Fields( "number", "lower" ), getOutputPath( "multisink/lhs" ), SinkMode.REPLACE );
    Tap rhsSink = getPlatform().getTextFile( new Fields( "offset", "line" ), new Fields( "number", "upper" ), getOutputPath( "/multisink/rhs" ), SinkMode.REPLACE );

    Tap sink = new MultiSinkTap( lhsSink, rhsSink );

    Flow flow = getPlatform().getFlowConnector().connect( source, sink, pipe );

    flow.complete();

    validateLength( flow.openTapForRead( lhsSink ), 5 );
    validateLength( flow.openTapForRead( rhsSink ), 5 );
    }

  public void testMultiSourceIterator() throws Exception
    {
    getPlatform().copyFromLocal( inputFileLower );
    getPlatform().copyFromLocal( inputFileUpper );

    Tap sourceLower = getPlatform().getTextFile( new Fields( "offset", "line" ), inputFileLower );
    Tap sourceUpper = getPlatform().getTextFile( new Fields( "offset", "line" ), inputFileUpper );

    Tap source = new MultiSourceTap( sourceLower, sourceUpper );

    validateLength( source.openForRead( getPlatform().getFlowProcess() ), 10 );
    }
  }
