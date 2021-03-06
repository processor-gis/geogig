/* Copyright (c) 2012-2016 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Gabriel Roldan (Boundless) - initial implementation
 */
package org.locationtech.geogig.test.integration.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.locationtech.geogig.model.ObjectId;
import org.locationtech.geogig.model.Ref;
import org.locationtech.geogig.model.impl.RevObjectTestSupport;
import org.locationtech.geogig.repository.Platform;
import org.locationtech.geogig.storage.RefDatabase;
import org.locationtech.geogig.test.TestPlatform;

public abstract class RefDatabaseTest {

    protected RefDatabase refDb;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    private static final ObjectId sampleId = RevObjectTestSupport.hashString("some random string");

    @Before
    public void setUp() throws Exception {
        File repoDir = tmpFolder.newFolder("repo", ".geogig");
        File workingDirectory = repoDir.getParentFile();
        File userHomeDirectory = tmpFolder.newFolder("home");
        Platform platform = new TestPlatform(workingDirectory, userHomeDirectory);
        refDb = createDatabase(platform);
        refDb.open();
    }

    @After
    public void tearDown() throws Exception {
        refDb.close();
    }

    protected abstract RefDatabase createDatabase(Platform platform) throws Exception;

    @Test
    public void testEmpty() {
        refDb.putRef(Ref.MASTER, ObjectId.NULL);
        refDb.putSymRef(Ref.HEAD, Ref.MASTER);
        assertEquals(ObjectId.NULL, refDb.get(Ref.MASTER).get().getObjectId());
        assertEquals(Ref.MASTER, refDb.get(Ref.HEAD).get().peel().getName());
    }

    @Test
    public void testPutGetRef() {
        byte[] raw = new byte[20];
        Arrays.fill(raw, (byte) 1);
        ObjectId oid = ObjectId.create(raw);

        assertFalse(refDb.get(Ref.MASTER).isPresent());

        refDb.putRef(Ref.MASTER, oid);
        assertEquals(oid, refDb.get(Ref.MASTER).get().getObjectId());

        refDb.putRef(Ref.WORK_HEAD, sampleId);
        assertEquals(sampleId, refDb.get(Ref.WORK_HEAD).get().getObjectId());
    }

    @Test
    public void testPutGetSymRef() {

        String branch = "refs/heads/branch";
        refDb.putRef(branch, sampleId);
        assertFalse(refDb.get(Ref.HEAD).isPresent());

        refDb.putSymRef(Ref.HEAD, branch);

        assertEquals(Ref.HEAD, refDb.get(Ref.HEAD).get().getName());
        assertEquals(branch, refDb.get(Ref.HEAD).get().peel().getName());
    }

    @Test
    public void testPutSymRefNonExistingTarget() {
        assertFalse(refDb.get(Ref.HEAD).isPresent());

        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("refs/heads/branch");
        refDb.putSymRef(Ref.HEAD, "refs/heads/branch");
    }

    @Test
    public void testRemove() {
        final String origin = Ref.append(Ref.ORIGIN, "master");
        refDb.putRef(origin, sampleId);
        refDb.putSymRef(Ref.HEAD, origin);

        assertEquals(sampleId, refDb.get(origin).get().getObjectId());
        assertEquals(origin, refDb.get(Ref.HEAD).get().peel().getName());

        assertEquals(origin, refDb.delete(Ref.HEAD).oldValue().get().peel().getName());
        assertFalse(refDb.get(Ref.HEAD).isPresent());
        assertEquals(sampleId, refDb.delete(origin).oldValue().get().getObjectId());
        assertFalse(refDb.get(origin).isPresent());
    }

    @Test
    public void testGetAll() {
        Map<String, Ref> allrefs = createTestRefs();
        Map<String, Ref> allOnNullNamespace = refDb.getAll().stream()
                .collect(Collectors.toMap(Ref::getName, r -> r));

        for (Ref ref : allrefs.values()) {
            String name = ref.getName();
            if (name.startsWith(Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace"))) {
                // createRefs added txnamespace1 and txnamespace2
                assertFalse(
                        name + " is in a transaction namespace, "
                                + "shall not be returned by getAll()",
                        allOnNullNamespace.containsKey(name));
            } else {
                assertTrue(name + " not found", allOnNullNamespace.containsKey(name));
                assertEquals(ref, allOnNullNamespace.get(name));
            }
        }
    }

    @Test
    public void testGetAllNullNamespace() {
        expected.expect(NullPointerException.class);
        refDb.getAll(null);
    }

    @Test
    public void testGetAllNonExistentNamespace() {
        List<Ref> all = refDb.getAll(Ref.append(Ref.TRANSACTIONS_PREFIX, "nonexistentns"));
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    public void testGetAllNamespace() {
        final String txNamespace1 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace1");
        final String txNamespace2 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace2");

        Map<String, Ref> allrefs = createTestRefs();
        Map<String, Ref> allOnNamespace;

        allOnNamespace = refDb.getAll(txNamespace1).stream()
                .collect(Collectors.toMap(Ref::getName, r -> r));
        assertNamespace(txNamespace1, allrefs, allOnNamespace);

        allOnNamespace = refDb.getAll(txNamespace2).stream()
                .collect(Collectors.toMap(Ref::getName, r -> r));
        assertNamespace(txNamespace2, allrefs, allOnNamespace);
    }

    private void assertNamespace(String namespace, Map<String, Ref> allrefs,
            Map<String, Ref> allOnNamespace) {

        for (Entry<String, Ref> e : allrefs.entrySet()) {
            String key = e.getKey();
            Ref value = e.getValue();
            if (key.startsWith(namespace)) {
                // createRefs added txnamespace1 and txnamespace2
                assertTrue(allOnNamespace.containsKey(key));
                assertEquals(value, allOnNamespace.get(key));
            } else {
                assertFalse(
                        key + " is NOT in a transaction namespace " + namespace
                                + ", shall not be returned by getAll(String namespace)",
                        allOnNamespace.containsKey(key));
            }
        }
    }

    @Test
    public void testRemoveAllNullNamespace() {
        expected.expect(NullPointerException.class);
        refDb.deleteAll(null);
    }

    @Test
    public void testRemoveAllNamespace() {
        final String txNamespace1 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace1");
        final String txNamespace2 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace2");
        final Map<String, Ref> allrefs = createTestRefs();

        Set<String> expected;
        Set<String> removed;

        expected = allrefs.keySet().stream().filter(n -> Ref.isChild(txNamespace1, n))
                .collect(Collectors.toSet());

        removed = refDb.deleteAll(txNamespace1).stream().map(Ref::getName)
                .collect(Collectors.toSet());

        assertEquals(expected, removed);

        expected = allrefs.keySet().stream().filter(n -> Ref.isChild(txNamespace2, n))
                .collect(Collectors.toSet());

        removed = refDb.deleteAll(txNamespace2).stream().map(Ref::getName)
                .collect(Collectors.toSet());

        assertEquals(expected, removed);
    }

    private Map<String, Ref> createTestRefs() {
        Map<String, Ref> refs = new TreeMap<>();

        // known root refs
        putRef(Ref.CHERRY_PICK_HEAD, sampleId, refs);
        putRef(Ref.ORIG_HEAD, sampleId, refs);
        putRef(Ref.MASTER, sampleId, refs);
        putSymRef(Ref.HEAD, "refs/heads/master", refs);
        putRef(Ref.WORK_HEAD, sampleId, refs);
        putRef(Ref.STAGE_HEAD, sampleId, refs);
        putRef(Ref.MERGE_HEAD, sampleId, refs);

        // some heads
        String branch1 = Ref.append(Ref.HEADS_PREFIX, "branch1");
        String tag1 = Ref.append(Ref.TAGS_PREFIX, "tag1");
        String remoteBranch1 = Ref.append(Ref.append(Ref.REMOTES_PREFIX, "r1"), "master");

        putRef(branch1, id("branch1"), refs);
        putRef(tag1, id("tag1"), refs);
        putRef(remoteBranch1, id("r1master"), refs);

        // some refs in a transaction namespace
        String txNamespace1 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace1");
        String txNamespace2 = Ref.append(Ref.TRANSACTIONS_PREFIX, "txnamespace2");

        String tx1Head = Ref.append(txNamespace1, Ref.HEAD);
        String tx1Master = Ref.append(txNamespace1, Ref.MASTER);
        putRef(tx1Head, id("tx1Head"), refs);
        putRef(tx1Master, id("tx1Master"), refs);

        String tx2Head = Ref.append(txNamespace2, Ref.HEAD);
        String tx2Master = Ref.append(txNamespace2, Ref.MASTER);
        putRef(tx2Head, id("tx2Head"), refs);
        putRef(tx2Master, id("tx2Master"), refs);
        return refs;
    }

    private ObjectId id(String string) {
        return RevObjectTestSupport.hashString(string);
    }

    private void putRef(String name, ObjectId value, Map<String, Ref> holder) {
        refDb.putRef(name, value);
        holder.put(name, new Ref(name, value));
    }

    private void putSymRef(String name, String value, Map<String, Ref> holder) {
        refDb.putSymRef(name, value);
        holder.put(name, refDb.get(name).get());
    }
}