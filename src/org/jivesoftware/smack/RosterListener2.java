/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * RosterListener2.java
 *
 */

package org.jivesoftware.smack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @Title: RosterListener2.java
 * @Package: org.jivesoftware.smack
 * @Description: 
 * 
 * @Author: aaron
 * @Date: 2014-6-12
 *
 */

public interface RosterListener2 {
    
    /**
     * callback when the roster is refreshed
     * 
     * */
    public void onRosterRefreshed();
    
    /**
     * new groups are added
     * 
     * */
    public void onGroupAdded(Collection<String> addedGroups);
    
    /**
     * groups are deleted
     * 
     * */
    public void onGroupRemoved(Collection<String> removedGroups);
    
    /**
     * roster entries are added
     * @param key, roster entries
     * @param value, groups that roster entry belongs to
     */
    public void onRostersAdded(HashMap<String, Set<String>> addedEntries);
    
    /**
     * roster entries are removed
     * @param key, roster entries
     * @param value, groups that roster entry belongs to
     * */
    public void onRostersRemoved(HashMap<String, Set<String>> removedEntries);

}
