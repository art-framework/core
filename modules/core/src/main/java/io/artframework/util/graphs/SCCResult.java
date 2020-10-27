/*
 * Copyright 2020 ART-Framework Contributors (https://github.com/Silthus/art-framework)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.artframework.util.graphs;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class SCCResult {
	private Set<Integer> nodeIDsOfSCC;
	private Vector<Integer>[] adjList;
	private int lowestNodeId;
	
	public SCCResult(Vector<Integer>[] adjList, int lowestNodeId) {
		this.adjList = adjList;
		this.lowestNodeId = lowestNodeId;
		this.nodeIDsOfSCC = new HashSet<>();
		if (this.adjList != null) {
			for (int i = this.lowestNodeId; i < this.adjList.length; i++) {
				if (this.adjList[i].size() > 0) {
					this.nodeIDsOfSCC.add(i);
				}
			}
		}
	}

	public Vector<Integer>[] getAdjList() {
		return adjList;
	}

	public int getLowestNodeId() {
		return lowestNodeId;
	}
}
