/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package jogamp.graph.font.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: KernTable.java,v 1.1.1.1 2004-12-05 23:14:48 davidsch Exp $
 */
public class KernTable implements Table {
    
    private DirectoryEntry de;
    private int version;
    private int nTables;
    private KernSubtable[] tables;

    /** Creates new KernTable */
    protected KernTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        version = di.readUnsignedShort();
        nTables = di.readUnsignedShort();
        tables = new KernSubtable[nTables];
        for (int i = 0; i < nTables; i++) {
            tables[i] = KernSubtable.read(di);
        }
    }

    public int getSubtableCount() {
        return nTables;
    }
    
    public KernSubtable getSubtable(int i) {
        return tables[i];
    }

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return kern;
    }

    /**
     * Get a directory entry for this table.  This uniquely identifies the
     * table in collections where there may be more than one instance of a
     * particular table.
     * @return A directory entry
     */
    public DirectoryEntry getDirectoryEntry() {
        return de;
    }
    
}
