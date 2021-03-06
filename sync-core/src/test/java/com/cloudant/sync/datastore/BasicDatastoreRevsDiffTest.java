package com.cloudant.sync.datastore;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class BasicDatastoreRevsDiffTest extends BasicDatastoreTestBase{

    @Test
    public void revsDiff_emptyInput_returnEmpty() {
        Map<String, Collection<String>> revs =
                datastore.revsDiff(HashMultimap.<String, String>create());
        Assert.assertTrue(revs.size() == 0);
    }

    @Test
    public void revsDiff_oneDocOneRev_returnNothing() throws IOException {
        MutableDocumentRevision revMut = new MutableDocumentRevision();
        revMut.body = bodyOne;
        BasicDocumentRevision rev = datastore.createDocumentFromRevision(revMut);
        Multimap<String, String> revs = HashMultimap.create();
        revs.put(rev.getId(), rev.getRevision());
        Map<String, Collection<String>> missingRevs = datastore.revsDiff(revs);
        Assert.assertTrue(missingRevs.size() == 0);
    }

    @Test
    public void revsDiff_oneDocOneRev_returnOne() throws IOException {
        MutableDocumentRevision revMut = new MutableDocumentRevision();
        revMut.body = bodyOne;
        BasicDocumentRevision rev = datastore.createDocumentFromRevision(revMut);
        Multimap<String, String> revs = HashMultimap.create();
        revs.put(rev.getId(), "2-a");
        Map<String, Collection<String>> missingRevs = datastore.revsDiff(revs);
        Assert.assertTrue(missingRevs.size() == 1);
        Assert.assertTrue(missingRevs.get(rev.getId()).contains("2-a"));
    }

    @Test
    public void revsDiff_oneDocTwoRevs_returnNothing() throws ConflictException, IOException {
        MutableDocumentRevision revMut1 = new MutableDocumentRevision();
        revMut1.body = bodyOne;
        BasicDocumentRevision rev1 = datastore.createDocumentFromRevision(revMut1);
        MutableDocumentRevision rev2Mut = rev1.mutableCopy();
        rev2Mut.body = bodyTwo;
        BasicDocumentRevision rev2 = datastore.updateDocumentFromRevision(rev2Mut);
        Multimap<String, String> revs = HashMultimap.create();
        revs.put(rev1.getId(), rev1.getRevision());
        revs.put(rev2.getId(), rev2.getRevision());
        Map<String, Collection<String>> missingRevs = datastore.revsDiff(revs);
        Assert.assertTrue(missingRevs.size() == 0);
    }

    @Test
    public void revsDiff_twoDoc_returnOneDoc() throws IOException {
        MutableDocumentRevision revMut1 = new MutableDocumentRevision();
        revMut1.body = bodyOne;
        BasicDocumentRevision rev1 = datastore.createDocumentFromRevision(revMut1);
        MutableDocumentRevision revMut2 = new MutableDocumentRevision();
        revMut2.body = bodyTwo;
        BasicDocumentRevision rev2 = datastore.createDocumentFromRevision(revMut2);
        Multimap<String, String> revs = HashMultimap.create();
        revs.put(rev1.getId(), rev1.getRevision());
        revs.put(rev1.getId(), "2-a");
        revs.put(rev2.getId(), rev2.getRevision());

        Map<String, Collection<String>> missingRevs = datastore.revsDiff(revs);
        Assert.assertTrue(missingRevs.size() == 1);
        Assert.assertTrue(missingRevs.get(rev1.getId()).contains("2-a"));
    }

    @Test
    public void revsDiff_twoDoc_returnTwoDocs() throws IOException {
        MutableDocumentRevision revMut1 = new MutableDocumentRevision();
        revMut1.body = bodyOne;
        BasicDocumentRevision rev1 = datastore.createDocumentFromRevision(revMut1);
        MutableDocumentRevision revMut2 = new MutableDocumentRevision();
        revMut2.body = bodyTwo;
        BasicDocumentRevision rev2 = datastore.createDocumentFromRevision(revMut2);

        Multimap<String, String> revs = HashMultimap.create();
        revs.put(rev1.getId(), rev1.getRevision());
        revs.put(rev1.getId(), "2-a");
        revs.put(rev2.getId(), rev2.getRevision());
        revs.put(rev2.getId(), "2-a");

        Map<String, Collection<String>> missingRevs = datastore.revsDiff(revs);
        Assert.assertTrue(missingRevs.size() == 2);
        Assert.assertTrue(missingRevs.get(rev1.getId()).contains("2-a"));
        Assert.assertTrue(missingRevs.get(rev2.getId()).contains("2-a"));
    }

    @Test
    public void revsDiff_oneDocWithManyRevisions_onlyNonExistingRevisionsReturned() throws IOException {
        MutableDocumentRevision revMut1 = new MutableDocumentRevision();
        revMut1.body = bodyOne;
        BasicDocumentRevision rev1 = datastore.createDocumentFromRevision(revMut1);
        MutableDocumentRevision revMut2 = new MutableDocumentRevision();
        revMut2.body = bodyTwo;
        BasicDocumentRevision rev2 = datastore.createDocumentFromRevision(revMut2);

        Multimap<String, String> revs = HashMultimap.create();
        // Add two existing revisions first, and then add many
        // revisions that do not exist yet. The two existing
        // revisions should not return in the api result.
        revs.put(rev1.getId(), rev1.getRevision());
        revs.put(rev2.getId(), rev2.getRevision());
        for(int i = 1 ; i < 100000 ; i ++) {
            revs.put(rev1.getId(), i + "-a");
        }
        Map<String, Collection<String>> missing = datastore.revsDiff(revs);
        Assert.assertEquals(1, missing.size());
        Assert.assertEquals(99999, missing.get(rev1.getId()).size());
        Assert.assertTrue(missing.get(rev1.getId()).contains("1-a"));
        Assert.assertTrue(missing.get(rev1.getId()).contains("499-a"));
        Assert.assertTrue(missing.get(rev1.getId()).contains("99999-a"));
        Assert.assertFalse(missing.get(rev1.getId()).contains(rev1.getRevision()));
    }

}
