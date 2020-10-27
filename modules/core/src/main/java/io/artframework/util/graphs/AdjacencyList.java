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

import java.util.Vector;


/**
 * Calculates the adjacency-list for a given adjacency-matrix.
 * 
 * 
 * @author Frank Meyer, web@normalisiert.de
 * @version 1.0, 26.08.2006
 *
 */
public class AdjacencyList {
	/**
	 * Calculates a adjacency-list for a given array of an adjacency-matrix.
	 *
	 * @param adjacencyMatrix array with the adjacency-matrix that represents
	 * the graph
	 * @return int[][]-array of the adjacency-list of given nodes. The first
	 * dimension in the array represents the same node as in the given
	 * adjacency, the second dimension represents the indicies of those nodes,
	 * that are direct successornodes of the node.
	 */
	public static int[][] getAdjacencyList(boolean[][] adjacencyMatrix) {
		int[][] list = new int[adjacencyMatrix.length][];

		for (int i = 0; i < adjacencyMatrix.length; i++) {
			Vector<Integer> v = new Vector<>();
			for (int j = 0; j < adjacencyMatrix[i].length; j++) {
				if (adjacencyMatrix[i][j]) {
					v.add(j);
				}
			}

			list[i] = new int[v.size()];
			for (int j = 0; j < v.size(); j++) {
				Integer in = v.get(j);
				list[i][j] = in;
			}
		}
		
		return list;
	}
}
