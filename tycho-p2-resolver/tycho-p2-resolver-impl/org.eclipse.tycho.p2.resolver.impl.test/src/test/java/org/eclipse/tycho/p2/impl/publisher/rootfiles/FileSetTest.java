package org.eclipse.tycho.p2.impl.publisher.rootfiles;

import java.io.File;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class FileSetTest {

    @Test
    public void testDoubleStar() {
        FileSet doubleStarAtEnd = new FileSet(null, "test/**");
        Assert.assertTrue(doubleStarAtEnd.matches(new Path("test/me/foo.txt")));
        Assert.assertFalse(doubleStarAtEnd.matches(new Path("me/foo.txt")));

        FileSet doubleStarWithSlashAtEnd = new FileSet(null, "test/**/");
        Assert.assertTrue(doubleStarWithSlashAtEnd.matches(new Path("test/me/foo.txt")));
        Assert.assertFalse(doubleStarWithSlashAtEnd.matches(new Path("me/foo.txt")));

        FileSet doubleStarAtBeginning = new FileSet(null, "**/FILE");
        Assert.assertTrue(doubleStarAtBeginning.matches(new Path("test/me/FILE")));

        FileSet doubleStarAtBeginningAndEnd = new FileSet(null, "**/DIR/**");
        Assert.assertTrue(doubleStarAtBeginningAndEnd.matches(new Path("test/me/DIR/bar/test.txt")));
        Assert.assertFalse(doubleStarAtBeginningAndEnd.matches(new Path("test/me/foobar/test.txt")));
        Assert.assertFalse(doubleStarAtBeginningAndEnd.matches(new Path("test/me/DIR")));
    }

    @Test
    public void testSingleStar() {
        FileSet starAtBeginning = new FileSet(null, "*.txt");
        Assert.assertTrue(starAtBeginning.matches(new Path("foo.txt")));

        FileSet starAtEnd = new FileSet(null, "bar*");
        Assert.assertTrue(starAtEnd.matches(new Path("barfoo")));
        Assert.assertFalse(starAtEnd.matches(new Path("foobar")));

        FileSet starInMiddle = new FileSet(null, "bar*foo");
        Assert.assertTrue(starInMiddle.matches(new Path("bar_test_foo")));
        Assert.assertFalse(starInMiddle.matches(new Path("bar_test_fooX")));
    }

    @Test
    public void testQuestionMark() {
        FileSet questionMarkPattern = new FileSet(null, "foo?.txt");
        Assert.assertTrue(questionMarkPattern.matches(new Path("fooX.txt")));
        Assert.assertFalse(questionMarkPattern.matches(new Path("fooXY.txt")));
        Assert.assertFalse(questionMarkPattern.matches(new Path("XfooY.txt")));
    }

    @Test
    public void testCombined() {
        FileSet recursiveTxtPattern = new FileSet(null, "**/*.txt");
        Assert.assertTrue(recursiveTxtPattern.matches(new Path("tmp/foo.txt")));
        Assert.assertTrue(recursiveTxtPattern.matches(new Path("foo.txt")));
        Assert.assertFalse(recursiveTxtPattern.matches(new Path("foo.txt_")));
        FileSet recursiveFilePrefixPattern = new FileSet(null, "**/prefix*");
        Assert.assertTrue(recursiveFilePrefixPattern.matches(new Path("tmp/prefixfoo.txt")));
    }

    @Test
    public void testDefaultExcludes() {
        FileSet recursiveFileSet = new FileSet(null, "test/**");
        Assert.assertTrue(recursiveFileSet.matches(new Path("test/me/foo.txt")));
        Assert.assertFalse(recursiveFileSet.matches(new Path("test/CVS/foo.txt")));
        Assert.assertFalse(recursiveFileSet.matches(new Path("test/.git/foo.txt")));
        Assert.assertFalse(recursiveFileSet.matches(new Path("test/.svn/foo.txt")));
        Assert.assertFalse(recursiveFileSet.matches(new Path("test/me/.svn")));
    }

    @Test
    public void testScan() {
        FileSet txtFileset = new FileSet(new File("resources/rootfiles"), "**/*.txt");
        Map<File, IPath> result = txtFileset.scan();
        Assert.assertEquals(4, result.size());
    }
}
