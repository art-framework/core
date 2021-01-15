/*
 *  Copyright 2020 ART-Framework Contributors (https://github.com/art-framework/)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.artframework.util.graphs;

import io.artframework.ModuleMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;



/**
 * Searchs all elementary cycles in a given directed graph. The implementation
 * is independent from the concrete objects that represent the graphnodes, it
 * just needs an array of the objects representing the nodes the graph
 * and an adjacency-matrix of type boolean, representing the edges of the
 * graph. It then calculates based on the adjacency-matrix the elementary
 * cycles and returns a list, which contains lists itself with the objects of the 
 * concrete graphnodes-implementation. Each of these lists represents an
 * elementary cycle.<p><p>
 *
 * The implementation uses the algorithm of Donald B. Johnson for the search of
 * the elementary cycles. For a description of the algorithm see:<p>
 * Donald B. Johnson: Finding All the Elementary Circuits of a Directed Graph.
 * SIAM Journal on Computing. Volumne 4, Nr. 1 (1975), pp. 77-84.<p><p>
 *
 * The algorithm of Johnson is based on the search for strong connected
 * components in a graph. For a description of this part see:<p>
 * Robert Tarjan: Depth-first search and linear graph algorithms. In: SIAM
 * Journal on Computing. Volume 1, Nr. 2 (1972), pp. 146-160.<p>
 * 
 * @author Frank Meyer, web_at_normalisiert_dot_de
 * @version 1.2, 22.03.2009
 *
 */
public class CycleSearch<TObject> {

	/**
	 * Creates a new cycle searcher that will search the dependency graph of the modules
	 * and returns a list of modules that have cyclic dependencies.
	 * <p>
	 * Use the {@link #getCycles()} method to get a list of all modules that have cyclic depencencies.
	 *
	 * @param modules list of modules to create dependency graph for
	 * @return the dependency graph of the modules
	 */
	public static CycleSearch<ModuleMeta> of(Collection<ModuleMeta> modules) {
		ModuleMeta[] array = modules.toArray(new ModuleMeta[0]);
		return new CycleSearch<>(getAdjacencyMatrix(array), array);
	}

	private static boolean[][] getAdjacencyMatrix(ModuleMeta[] modules) {
		boolean[][] adjMatrix = new boolean[modules.length][modules.length];

		for (int i = 0; i < modules.length; i++) {
			Integer[] dependencyMatrix = getDependencyMatrix(modules, modules[i]);
			for (int d = 0; d < dependencyMatrix.length; d++) {
				adjMatrix[i][dependencyMatrix[d]] = true;
			}
		}

		return adjMatrix;
	}

	private static Integer[] getDependencyMatrix(ModuleMeta[] modules, ModuleMeta module) {

		List<Integer> dependencyIdicies = new ArrayList<>();

		for (String dependency : module.dependencies()) {
			for (int i = 0; i < modules.length; i++) {
				ModuleMeta mod = modules[i];
				if (mod.identifier().equals(dependency)) {
					dependencyIdicies.add(i);
				}
			}
		}

		return dependencyIdicies.toArray(new Integer[0]);
	}

	/** List of cycles */
	private List<List<TObject>> cycles = null;

	/** Adjacency-list of graph */
	private int[][] adjList;

	/** Graphnodes */
	private TObject[] graphNodes;

	/** Blocked nodes, used by the algorithm of Johnson */
	private boolean[] blocked = null;

	/** B-Lists, used by the algorithm of Johnson */
	private Vector<Integer>[] B = null;

	/** Stack for nodes, used by the algorithm of Johnson */
	private Vector<Integer> stack = null;

	/**
	 * Constructor.
	 *
	 * @param matrix adjacency-matrix of the graph
	 * @param graphNodes array of the graphnodes of the graph; this is used to
	 * build sets of the elementary cycles containing the objects of the original
	 * graph-representation
	 */
	public CycleSearch(boolean[][] matrix, TObject[] graphNodes) {
		this.graphNodes = graphNodes;
		this.adjList = AdjacencyList.getAdjacencyList(matrix);
	}

	/**
	 * Returns List::List::Object with the Lists of nodes of all elementary
	 * cycles in the graph.
	 *
	 * @return List::List::Object with the Lists of the elementary cycles.
	 */
	public List<List<TObject>> getCycles() {

		if (this.cycles != null) return this.cycles;

		this.cycles = new Vector<>();
		this.blocked = new boolean[this.adjList.length];
		this.B = new Vector[this.adjList.length];
		this.stack = new Vector<>();
		StrongConnectedComponents sccs = new StrongConnectedComponents(this.adjList);
		int s = 0;

		while (true) {
			SCCResult sccResult = sccs.getAdjacencyList(s);
			if (sccResult != null && sccResult.getAdjList() != null) {
				Vector<Integer>[] scc = sccResult.getAdjList();
				s = sccResult.getLowestNodeId();
				for (int j = 0; j < scc.length; j++) {
					if ((scc[j] != null) && (scc[j].size() > 0)) {
						this.blocked[j] = false;
						this.B[j] = new Vector<>();
					}
				}

				this.findCycles(s, s, scc);
				s++;
			} else {
				break;
			}
		}

		return this.cycles;
	}

	/**
	 * Calculates the cycles containing a given node in a strongly connected
	 * component. The method calls itself recursivly.
	 *
	 * @param v
	 * @param s
	 * @param adjList adjacency-list with the subgraph of the strongly
	 * connected component s is part of.
	 * @return true, if cycle found; false otherwise
	 */
	private boolean findCycles(int v, int s, Vector<Integer>[] adjList) {
		boolean f = false;
		this.stack.add(v);
		this.blocked[v] = true;

		for (int i = 0; i < adjList[v].size(); i++) {
			int w = adjList[v].get(i);
			// found cycle
			if (w == s) {
				Vector<TObject> cycle = new Vector<>();
				for (int index : this.stack) {
					cycle.add(this.graphNodes[index]);
				}
				this.cycles.add(cycle);
				f = true;
			} else if (!this.blocked[w]) {
				if (this.findCycles(w, s, adjList)) {
					f = true;
				}
			}
		}

		if (f) {
			this.unblock(v);
		} else {
			for (int i = 0; i < adjList[v].size(); i++) {
				int w = adjList[v].get(i);
				if (!this.B[w].contains(v)) {
					this.B[w].add(v);
				}
			}
		}

		this.stack.remove(new Integer(v));
		return f;
	}

	/**
	 * Unblocks recursivly all blocked nodes, starting with a given node.
	 *
	 * @param node node to unblock
	 */
	private void unblock(int node) {
		this.blocked[node] = false;
		Vector<Integer> Bnode = this.B[node];
		while (Bnode.size() > 0) {
			Integer w = Bnode.get(0);
			Bnode.remove(0);
			if (this.blocked[w]) {
				this.unblock(w);
			}
		}
	}
}

