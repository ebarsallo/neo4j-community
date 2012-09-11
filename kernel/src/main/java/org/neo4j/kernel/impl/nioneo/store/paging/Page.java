/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.nioneo.store.paging;

class Page
{
    final int address;

    boolean referenced = false;
    TemporalUtility utility = TemporalUtility.UNKNOWN;

    CachedPageList currentList = null;
    Page prevPage;
    Page nextPage;

    Page( int address )
    {
        this.address = address;
    }

    Page moveToTailOf( CachedPageList targetList )
    {
        if ( currentList != null )
        {
            if ( prevPage != null )
            {
                prevPage.nextPage = nextPage;
            }
            if ( nextPage != null )
            {
                nextPage.prevPage = prevPage;
            }
            if ( currentList.head == this )
            {
                currentList.head = nextPage;
            }
            if ( currentList.tail == this )
            {
                currentList.tail = prevPage;
            }
            currentList.decrementSize();
        }
        prevPage = targetList.tail;
        if ( prevPage != null )
        {
            prevPage.nextPage = this;
        }
        nextPage = null;
        targetList.tail = this;
        if ( targetList.head == null )
        {
            targetList.head = this;
        }
        targetList.incrementSize();
        this.currentList = targetList;

        return this;
    }

    Page setReferenced()
    {
        referenced = true;
        return this;
    }

    Page clearReference()
    {
        referenced = false;
        return this;
    }

    Page setUtility( TemporalUtilityCounter counter, TemporalUtility utility )
    {
        counter.decrement( this.utility );
        counter.increment( this.utility = utility );
        return this;
    }

    @Override
    public String toString()
    {
        return String.format( "Page{address=%d, inAList=%b}", address, currentList != null );
    }
}
