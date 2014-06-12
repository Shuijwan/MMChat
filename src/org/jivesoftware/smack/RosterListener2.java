/**
 *
 * Copyright 2014 Cisco Inc. All rights reserved.
 * RosterListener2.java
 *
 */

package org.jivesoftware.smack;

import android.util.Pair;

import java.util.ArrayList;
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
    
    public void onRosterRefreshed();
    
    public void onGroupAdded(Collection<String> addedGroups);
    
    public void onGroupRemoved(Collection<String> removedGroups);
    
    public void onRostersAdded(HashMap<String, Set<String>> addedEntries);
    
    public void onRostersRemoved(HashMap<String, Set<String>> removedEntries);

}
