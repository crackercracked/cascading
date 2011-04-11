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

import cascading.flow.FlowProcess;
import cascading.scheme.Scheme;
import cascading.tuple.Fields;
import cascading.tuple.TupleEntryCollector;
import cascading.tuple.TupleEntryIterator;

/**
 * Class SinkTap is the base class for sink only Taps. Some {@link cascading.tap.Tap} instances may only be sinks (as opposed
 * to being a source). These types should subclass SinkTap for convenience.
 */
public abstract class SinkTap<Process extends FlowProcess, Config, Input, Output> extends Tap<Process, Config, Input, Output>
  {
  protected SinkTap()
    {
    }

  protected SinkTap( Scheme scheme )
    {
    super( scheme );
    }

  protected SinkTap( Scheme scheme, SinkMode sinkMode )
    {
    super( scheme, sinkMode );
    }

  @Override
  public Fields getSourceFields()
    {
    throw new UnsupportedOperationException( "unable to source tuple streams via a SinkTap instance" );
    }

  @Override
  public boolean isSource()
    {
    return false;
    }

  @Override
  public TupleEntryIterator openForRead( Process flowProcess, Input input ) throws IOException
    {
    throw new UnsupportedOperationException( "unable to open for read via a SinkTap instance" );
    }

  @Override
  public TupleEntryCollector openForWrite( Process flowProcess, Output output ) throws IOException
    {
    throw new UnsupportedOperationException( "unable to open for write via a SinkTap instance" );
    }

  @Override
  public void sourceConfInit( Process flowProcess, Config conf ) throws IOException
    {
    throw new UnsupportedOperationException( "unable to source tuple streams via a SinkTap instance" );
    }
  }