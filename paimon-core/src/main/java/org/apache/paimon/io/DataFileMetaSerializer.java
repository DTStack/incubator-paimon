/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.io;

import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.GenericRow;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.stats.BinaryTableStats;
import org.apache.paimon.utils.ObjectSerializer;

import static org.apache.paimon.utils.RowDataUtils.fromStringArrayData;
import static org.apache.paimon.utils.RowDataUtils.toStringArrayData;
import static org.apache.paimon.utils.SerializationUtils.deserializeBinaryRow;
import static org.apache.paimon.utils.SerializationUtils.serializeBinaryRow;

/** Serializer for {@link DataFileMeta}. */
public class DataFileMetaSerializer extends ObjectSerializer<DataFileMeta> {

    private static final long serialVersionUID = 1L;

    public DataFileMetaSerializer() {
        super(DataFileMeta.schema());
    }

    @Override
    public InternalRow toRow(DataFileMeta meta) {
        return GenericRow.of(
                BinaryString.fromString(meta.fileName()),
                meta.fileSize(),
                meta.rowCount(),
                serializeBinaryRow(meta.minKey()),
                serializeBinaryRow(meta.maxKey()),
                meta.keyStats().toRowData(),
                meta.valueStats().toRowData(),
                meta.minSequenceNumber(),
                meta.maxSequenceNumber(),
                meta.schemaId(),
                meta.level(),
                toStringArrayData(meta.extraFiles()),
                meta.creationTime());
    }

    @Override
    public DataFileMeta fromRow(InternalRow row) {
        return new DataFileMeta(
                row.getString(0).toString(),
                row.getLong(1),
                row.getLong(2),
                deserializeBinaryRow(row.getBinary(3)),
                deserializeBinaryRow(row.getBinary(4)),
                BinaryTableStats.fromRowData(row.getRow(5, 3)),
                BinaryTableStats.fromRowData(row.getRow(6, 3)),
                row.getLong(7),
                row.getLong(8),
                row.getLong(9),
                row.getInt(10),
                fromStringArrayData(row.getArray(11)),
                row.getTimestamp(12, 3));
    }
}
