package com.cloudant.sync.replication;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by tomblench on 10/12/14.
 */
public class ReplicatorIdTest extends ReplicationTestBase {

    // source/target switched, so ids should be different
    @Test
    public void testPullNotEqualToPush() throws URISyntaxException {
        PullReplication pull = this.createPullReplication();
        PushReplication push = this.createPushReplication();
        Assert.assertNotEquals(pull.createReplicationStrategy().getReplicationId(), push.createReplicationStrategy().getReplicationId());
    }

    // two pull reps identical
    @Test
    public void testPullsEqual() throws URISyntaxException {
        PullReplication pull1 = this.createPullReplication();
        PullReplication pull2 = this.createPullReplication();
        Assert.assertEquals(pull1.createReplicationStrategy().getReplicationId(), pull2.createReplicationStrategy().getReplicationId());
    }

    // two push reps identical
    @Test
    public void testPushesEqual() throws URISyntaxException {
        PushReplication push1 = this.createPushReplication();
        PushReplication push2 = this.createPushReplication();
        Assert.assertEquals(push1.createReplicationStrategy().getReplicationId(), push2.createReplicationStrategy().getReplicationId());
    }
}
