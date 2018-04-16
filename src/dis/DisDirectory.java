package dis;

import org.apache.lucene.store.*;
import org.apache.lucene.util.IOUtils;
import org.elasticsearch.common.util.concurrent.ConcurrentCollections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: Zhy
 * Mail: zhy@unimassystem.com
 * Date: 14-11-13
 * Time: 上午10:55
 */
public class DisDirectory extends Directory {
    protected Directory[] delegates;
    private final ConcurrentMap<String, Directory> nameDirMapping = ConcurrentCollections.newConcurrentMap();

    public DisDirectory(String[] filepaths) throws IOException {
        delegates = new Directory[filepaths.length];
        for(int i=0; i<filepaths.length; i++) {
            File fPath = new File(filepaths[i]);
            if(fPath.exists()) {
                delegates[i] = FSDirectory.open(Paths.get(fPath.getAbsolutePath()));
                for (String file : delegates[i].listAll()) {
                    nameDirMapping.put(file, delegates[i]);
                }
            }
        }
    }

    @Override
    public String[] listAll() throws IOException {
        final ArrayList<String> files = new ArrayList<>();
        for (Directory dir : delegates) {
            if(dir!=null)
                Collections.addAll(files, dir.listAll());
        }
        return files.toArray(new String[files.size()]);
    }

    private Directory getDirectory(String name) throws IOException {
        Directory directory = nameDirMapping.get(name);
        if (directory == null) {
            throw new FileNotFoundException("No such file [" + name + "]");
        }
        return directory;
    }

    @Override
    public void deleteFile(String name) throws IOException {
        getDirectory(name).deleteFile(name);
        Directory remove = nameDirMapping.remove(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return getDirectory(name).fileLength(name);
    }

    @Override
    public IndexOutput createOutput(String name, IOContext context) throws IOException {
        return getDirectory(name).createOutput(name, context);
    }

    @Override
    public IndexOutput createTempOutput(String prefix, String suffix, IOContext context) throws IOException {
        return null;
    }

    @Override
    public void sync(Collection<String> names) throws IOException {
        for (Directory dir : delegates) {
            if(dir!=null)
                dir.sync(names);
        }
    }

    @Override
    public void rename(String from, String to) throws IOException {
        getDirectory(from).rename(from, to);
    }

    @Override
    public void syncMetaData() throws IOException {

    }

    @Override
    public IndexInput openInput(String name, IOContext context) throws IOException {
        return getDirectory(name).openInput(name, context);
    }

    @Override
    public Lock obtainLock(String s) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(delegates);
    }
}
