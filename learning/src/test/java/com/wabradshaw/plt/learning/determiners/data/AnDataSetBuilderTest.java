package com.wabradshaw.plt.learning.determiners.data;

import com.wabradshaw.plt.learning.determiners.data.AnDataSetBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A set of tests for the {@link com.wabradshaw.plt.learning.determiners.data.AnDataSetBuilder}
 */
class AnDataSetBuilderTest {

    private static final String EMPTY = "empty.txt";
    private static final String SINGLE_A = "single_a.txt";
    private static final String SINGLE_AN = "single_an.txt";
    private static final String MULTIPLE_A = "multiple_a.txt";
    private static final String MULTIPLE_AN = "multiple_an.txt";
    private static final String CLASH_A = "clash_a.txt";
    private static final String CLASH_AN = "clash_an.txt";
    private static final String MULTI_CLASH_A = "multiclash_a.txt";
    private static final String MULTI_CLASH_AN = "multiclash_an.txt";
    private static final String BIG_A = "big_a.txt";
    private static final String BIG_AN = "big_an.txt";

    /**
     * Tests that the builder can produce a single output file.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testCreateOneFile(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(SINGLE_A), load(SINGLE_AN), tempDir, 1);
        assertEquals(1, tempDir.listFiles().length);
        assertEquals("0.txt", getFile(tempDir,0).getName());
    }

    /**
     * Tests that the builder can produce multiple output files.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testCreateMultipleFiles(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(SINGLE_A), load(SINGLE_AN), tempDir, 3);
        assertEquals(3, tempDir.listFiles().length);
        assertEquals("0.txt", getFile(tempDir,0).getName());
        assertEquals("1.txt", getFile(tempDir,1).getName());
        assertEquals("2.txt", getFile(tempDir,2).getName());
    }

    /**
     * Tests that the builder can handle a single A entry.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testSingleA(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(SINGLE_A), load(EMPTY), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(1, content.size());
        assertEquals("cat\t0.0", content.get(0));
    }

    /**
     * Tests that the builder can handle a single An entry
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testSingleAn(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(EMPTY), load(SINGLE_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(1, content.size());
        assertEquals("owl\t1.0", content.get(0));
    }

    /**
     * Tests that the builder can handle a single A at the same time as a single An entry
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testSingleASingleAn(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(SINGLE_A), load(SINGLE_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(2, content.size());
        assertEquals("cat\t0.0", content.get(0));
        assertEquals("owl\t1.0", content.get(1));
    }

    /**
     * Tests that the builder can handle multiple A entries.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testMultipleA(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(MULTIPLE_A), load(EMPTY), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(3, content.size());
        assertEquals("cat\t0.0", content.get(0));
        assertEquals("cow\t0.0", content.get(1));
        assertEquals("zebra\t0.0", content.get(2));
    }

    /**
     * Tests that the builder can handle multiple An entries
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testMultipleAn(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(EMPTY), load(MULTIPLE_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(3, content.size());
        assertEquals("abacus\t1.0", content.get(0));
        assertEquals("orange\t1.0", content.get(1));
        assertEquals("ordinal direction\t1.0", content.get(2));
    }

    /**
     * Tests that if a word appears in both a and an, the ratio will handle this.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testSingleClash(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(CLASH_A), load(CLASH_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(1, content.size());
        assertEquals("herb\t0.4", content.get(0));
    }

    /**
     * Tests that if a word appears several times in both a and an, the ratio will handle this.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testMulticlash(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(MULTI_CLASH_A), load(MULTI_CLASH_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(1, content.size());
        assertEquals("herb\t0.7", content.get(0));
    }

    /**
     * Tests that the builder can handle multiple distinct As and Ans
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testMultipleAMultipleAn(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(MULTIPLE_A), load(MULTIPLE_AN), tempDir, 1);
        List<String> content =  getContent(tempDir, 0);
        assertEquals(6, content.size());
        assertEquals("abacus\t1.0", content.get(0));
        assertEquals("cat\t0.0", content.get(1));
        assertEquals("cow\t0.0", content.get(2));
        assertEquals("orange\t1.0", content.get(3));
        assertEquals("ordinal direction\t1.0", content.get(4));
        assertEquals("zebra\t0.0", content.get(5));
    }

    /**
     * Tests that reasonably large files don't break the system.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testBig(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(BIG_A), load(BIG_AN), tempDir, 1);
        List<String> content = getContent(tempDir, 0);
        assertEquals(819, content.size());
    }

    /**
     * Tests that a result can be split across multiple output datasets. Each dataset should have at least a few items
     * in (they'll tend to be about 20%, but I don't want rare failing tests due to chance). A and An should be
     * interspersed.
     *
     * @param tempDir Temporary output directory.
     */
    @Test
    void testMultipleDatasets(@TempDir File tempDir) throws IOException {
        new AnDataSetBuilder().build(load(BIG_A), load(BIG_AN), tempDir, 5);

        int total = 0;
        for (int i = 0; i < 5; i++) {
            List<String> content = getContent(tempDir, i);
            assertTrue(content.size() > 20);

            assertTrue(content.stream().anyMatch(c -> c.contains("\t1.0")));
            assertTrue(content.stream().anyMatch(c -> c.contains("\t0.0")));
            total += content.size();
        }
        assertEquals(819, total);
    }

    /**
     * Creates a {@link File} representing the named resource in the determiners folder
     *
     * @param resource The name of the file
     * @return The File object
     */
    private File load(String resource){
        return new File(this.getClass().getResource("/determiners/" + resource).getPath());
    }

    /**
     * Gets a dataset {@link File} from the temporary folder by index.
     *
     * @param tempDir The directory with the {@link File}
     * @param i The index of the dataset
     * @return The File object
     */
    private File getFile(File tempDir, int i){
        return new File(tempDir.getPath() + "/" + i + ".txt");
    }

    /**
     * Gets a list of all of the lines within the dataset file at a particular index
     *
     * @param tempDir The directory with the {@link File}
     * @param i The index of the dataset
     * @return The contents of the file
     */
    private List<String> getContent(File tempDir, int i) throws IOException {
        return FileUtils.readLines(getFile(tempDir, i), Charset.defaultCharset());
    }
}
