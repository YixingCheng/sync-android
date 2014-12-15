package com.cloudant.sync.replication;

import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tomblench on 10/12/14.
 */
public class ReplicatorIdTest extends ReplicationTestBase {

    // source/target switched, so ids should be different
    @Test
    public void pullNotEqualToPush() throws URISyntaxException {
        PullReplication pull = this.createPullReplication();
        PushReplication push = this.createPushReplication();
        Assert.assertNotEquals(pull.createReplicationStrategy().getReplicationId(), push.createReplicationStrategy().getReplicationId());
    }

    // two pull reps identical
    @Test
    public void pullsEqual() throws URISyntaxException {
        PullReplication pull1 = this.createPullReplication();
        PullReplication pull2 = this.createPullReplication();
        Assert.assertEquals(pull1.createReplicationStrategy().getReplicationId(), pull2.createReplicationStrategy().getReplicationId());
    }

    // two push reps identical
    @Test
    public void pushesEqual() throws URISyntaxException {
        PushReplication push1 = this.createPushReplication();
        PushReplication push2 = this.createPushReplication();
        Assert.assertEquals(push1.createReplicationStrategy().getReplicationId(), push2.createReplicationStrategy().getReplicationId());
    }

    // two push reps with differing target not equal
    @Test
    public void pushesDifferingTargetNotEqual() throws URISyntaxException {
        PushReplication push1 = this.createPushReplication();
        PushReplication push2 = this.createPushReplicationDifferingTarget();
        Assert.assertNotEquals(push1.createReplicationStrategy().getReplicationId(), push2.createReplicationStrategy().getReplicationId());
    }

    // two pull reps with differing source not equal
    @Test
    public void pullsDifferingSourceNotEqual() throws URISyntaxException {
        PullReplication pull1 = this.createPullReplication();
        PullReplication pull2 = this.createPullReplicationDifferingSource();
        Assert.assertNotEquals(pull1.createReplicationStrategy().getReplicationId(), pull2.createReplicationStrategy().getReplicationId());
    }

    // two pull reps, one with filter, one without, not equal
    @Test
    public void pullWithFilterNotEqual() throws URISyntaxException {
        PullReplication pull1 = this.createPullReplication();
        PullReplication pull2 = this.createPullReplicationWithFilter1();
        Assert.assertNotEquals(pull1.createReplicationStrategy().getReplicationId(), pull2.createReplicationStrategy().getReplicationId());
    }

    // two pull reps, both with different filters, not equal
    @Test
    public void pullWithDifferentFiltersNotEqual() throws URISyntaxException {
        PullReplication pull1 = this.createPullReplicationWithFilter1();
        PullReplication pull2 = this.createPullReplicationWithFilter2();
        Assert.assertNotEquals(pull1.createReplicationStrategy().getReplicationId(), pull2.createReplicationStrategy().getReplicationId());
    }

    PullReplication createPullReplicationDifferingSource() throws URISyntaxException {
        PullReplication pullReplication = new PullReplication();
        pullReplication.username = this.getCouchConfig().getUsername();
        pullReplication.password = this.getCouchConfig().getPassword();
        pullReplication.source = new URI("http://a-different-source/a-database");
        pullReplication.target = this.datastore;
        return pullReplication;
    }

    PushReplication createPushReplicationDifferingTarget() throws URISyntaxException {
        PushReplication pushReplication = new PushReplication();
        pushReplication.username = this.getCouchConfig().getUsername();
        pushReplication.password = this.getCouchConfig().getPassword();
        pushReplication.target = new URI("http://a-different-target/a-database");
        pushReplication.source = this.datastore;
        return pushReplication;
    }

    PullReplication createPullReplicationWithFilter1() throws URISyntaxException {
        PullReplication pullReplication = new PullReplication();
        pullReplication.username = this.getCouchConfig().getUsername();
        pullReplication.password = this.getCouchConfig().getPassword();
        pullReplication.source = this.getURI();
        pullReplication.target = this.datastore;
        pullReplication.filter = new Replication.Filter("animal/by_class",
                ImmutableMap.of("class", "mammal"));
        return pullReplication;
    }

    PullReplication createPullReplicationWithFilter2() throws URISyntaxException {
        PullReplication pullReplication = new PullReplication();
        pullReplication.username = this.getCouchConfig().getUsername();
        pullReplication.password = this.getCouchConfig().getPassword();
        pullReplication.source = this.getURI();
        pullReplication.target = this.datastore;
        pullReplication.filter = new Replication.Filter("animal/by_class",
                ImmutableMap.of("class", "bird"));
        return pullReplication;
    }

}
