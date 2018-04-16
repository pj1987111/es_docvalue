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
public class DocValuesTest {
    public static void docValuesTest2(String pathParam) throws IOException {
        System.out.println("Test Get");
        Path path = Paths.get(pathParam);
        Directory fsDir = NIOFSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(fsDir);
        LeafReader leafReader = reader.leaves().get(0).reader();
        String field = "age";
        List<Integer> docIds = new ArrayList<>();
//        docIds.add(5);
        docIds.add(0);
        docIds.add(1);
//        docIds.add(2);
//        docIds.add(3);
//        docIds.add(6);
//        docIds.add(4);
//        docIds.add(7);
        SortedNumericDocValues values = leafReader.getSortedNumericDocValues(field);
        for(int docId : docIds) {
            values.setDocument(docId);
            System.out.println("SORTED_NUMERIC "+field+" : "+values.valueAt(docId));
        }

        docIds.clear();
        leafReader = reader.leaves().get(1).reader();
        docIds.add(1);
        docIds.add(2);
        docIds.add(3);
        values = leafReader.getSortedNumericDocValues(field);
        for(int docId : docIds) {
            values.setDocument(docId);
            System.out.println("SORTED_NUMERIC "+field+" : "+values.valueAt(docId));
        }
    }

    public static void docValuesUtest(String pathParam) throws IOException {
        Path path = Paths.get(pathParam);
        Directory fsDir = NIOFSDirectory.open(path);
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
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            values.setDocument(docId);
                            BytesRef ordVal = values.lookupOrd(values.nextOrd());
                            System.out.println("SORTED_SET docid : " + docId + " " + field + " : " + ordVal.utf8ToString());
                        }
                    }
                } else if (docValuesType == DocValuesType.SORTED_NUMERIC) {
                    SortedNumericDocValues values = leafReader.getSortedNumericDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            values.setDocument(docId);
                            System.out.println("SORTED_NUMERIC docid : " + docId + " " + field + " : " + values.valueAt(docId));
                        }
                    }
                } else if (docValuesType == DocValuesType.SORTED) {
                    SortedDocValues values = leafReader.getSortedDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("SORTED docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        }
                    }
                } else if (docValuesType == DocValuesType.BINARY) {
                    BinaryDocValues values = leafReader.getBinaryDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("BINARY docid : " + docId + " " + field + " : " + values.get(docId).utf8ToString());
                        }
                    }
                } else if (docValuesType == DocValuesType.NUMERIC) {
                    NumericDocValues values = leafReader.getNumericDocValues(field);
                    for (int docId = 0; docId < leafReader.maxDoc(); docId++) {
                        if ((liveDocs == null) || (liveDocs.get(docId))) {
                            System.out.println("NUMERIC docid : " + docId + " " + field + " : " + values.get(docId));
                        }
                    }
                }
            }
        }
    }



    static void hashMapUtest()
            throws IOException {
        Map<String, AbstractType> typeMapping = new HashMap();
        typeMapping.put("double", DoubleType.DOUBLE);
        typeMapping.put("float", RealType.REAL);
        typeMapping.put("integer", IntegerType.INTEGER);
        typeMapping.put("long", BigintType.BIGINT);
        typeMapping.put("date", DateType.DATE);
        typeMapping.put("timestamp", TimestampType.TIMESTAMP);
        typeMapping.put("keyword", VarcharType.VARCHAR);

        for (Map.Entry<String, AbstractType> entry : typeMapping.entrySet())
            if (entry.getValue() == DoubleType.DOUBLE)
                System.out.println(entry.getKey());
    }

    public static void main(String[] args) throws Exception {
//        StringBuffer s = new StringBuffer("s");
//        s.append("hello");
//        System.out.println(s.toString());
//        Slices.utf8Slice("hello");
        String path = "C:\\work\\WorkSpace\\index\\es_docvalue\\testdata\\test3\\1_1";
        docValuesUtest(path);
//        docValuesTest2(path);
    }
}
