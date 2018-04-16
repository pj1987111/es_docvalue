package dis;

import com.facebook.presto.spi.type.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by zhy on 2018/4/10.
 */
public class DocValuesTest2 {
    public static void docValuesUtest(String[] dataPaths) throws IOException {
        boolean printUnliveDocs = true;
        Directory fsDir = new DisDirectory(dataPaths);
        IndexReader reader = DirectoryReader.open(fsDir);
        for (LeafReaderContext context : reader.leaves()) {
            LeafReader leafReader = context.reader();
            FieldInfos infos = leafReader.getFieldInfos();
            Map<String, DocValuesType> fieldDocValueType = new HashMap<>();
            Iterator it = infos.iterator();
            while (it.hasNext()) {
                FieldInfo info = (FieldInfo) it.next();
                fieldDocValueType.put(info.name, info.getDocValuesType());
                System.out.print(info.name + ":" + info.getDocValuesType() + "=====");
            }
            System.out.println();
            Bits liveDocs = leafReader.getLiveDocs();
            System.out.println("Start...." + leafReader);

            for (Map.Entry<String, DocValuesType> fieldDocValueTypeEntry : fieldDocValueType.entrySet()) {
                String field = fieldDocValueTypeEntry.getKey();
                DocValuesType docValuesType = fieldDocValueTypeEntry.getValue();
                if (docValuesType == DocValuesType.SORTED_SET) {
                    SortedSetDocValues values = leafReader.getSortedSetDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        values.setDocument(docId);
                        long ord = values.nextOrd();
                        BytesRef ordVal = values.lookupOrd(ord);
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("SORTED_SET docid : " + docId + " " + field + " : " + ordVal.utf8ToString()+" ord: "+ord);
                        } else if(printUnliveDocs) {
                            System.out.println("unlived SORTED_SET docid : " + docId + " " + field + " : " + ordVal.utf8ToString()+" ord: "+ord);
                        }
                    }
                } else if (docValuesType == DocValuesType.SORTED_NUMERIC) {
                    SortedNumericDocValues values = leafReader.getSortedNumericDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        values.setDocument(docId);
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("SORTED_NUMERIC docid : " + docId + " " + field + " : " + values.valueAt(docId));
                        } else if(printUnliveDocs) {
                            System.out.println("unlived SORTED_NUMERIC docid : " + docId + " " + field + " : " + values.valueAt(docId));
                        }
                    }
                } else if (docValuesType == DocValuesType.SORTED) {
                    SortedDocValues values = leafReader.getSortedDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("SORTED docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        } else if(printUnliveDocs) {
                            System.out.println("unlived SORTED docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        }
                    }
                } else if (docValuesType == DocValuesType.BINARY) {
                    BinaryDocValues values = leafReader.getBinaryDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("BINARY docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        } else if(printUnliveDocs) {
                            System.out.println("unlived BINARY docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        }
                    }
                } else if (docValuesType == DocValuesType.NUMERIC) {
                    NumericDocValues values = leafReader.getNumericDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("NUMERIC docid : " + docId + " " + field + " : " + values.get(docId));
                        } else if(printUnliveDocs) {
                            System.out.println("unlived NUMERIC docid : " + docId + " " + field + " : " + values.get(docId));
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        StringBuffer s = new StringBuffer("s");
//        s.append("hello");
//        System.out.println(s.toString());
//        Slices.utf8Slice("hello");
        String paths[] = {"C:\\work\\WorkSpace\\index\\es_docvalue\\testdata\\test3\\1_1",
                "C:\\work\\WorkSpace\\index\\es_docvalue\\testdata\\test3\\1_2"};
        docValuesUtest(paths);
//        docValuesTest2(path);
    }
}
