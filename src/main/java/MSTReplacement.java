import java.util.*;

public class MSTReplacement {

    static class Edge implements Comparable<Edge> {
        int u, v, w;

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }

        public int compareTo(Edge o) {
            return this.w - o.w;
        }

        public String toString() {
            return "(" + u + "-" + v + ": " + w + ")";
        }
    }

    static class DSU {
        int[] p, r;

        DSU(int n) {
            p = new int[n];
            r = new int[n];
            for (int i = 0; i < n; i++) p[i] = i;
        }

        int find(int x) {
            if (p[x] != x) p[x] = find(p[x]);
            return p[x];
        }

        boolean unite(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return false;

            if (r[px] < r[py]) p[px] = py;
            else if (r[px] > r[py]) p[py] = px;
            else {
                p[py] = px;
                r[px]++;
            }
            return true;
        }
    }

    static List<Edge> kruskal(int n, List<Edge> edges) {
        Collections.sort(edges);
        DSU dsu = new DSU(n);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : edges) {
            if (dsu.unite(e.u, e.v)) {
                mst.add(e);
            }
        }
        return mst;
    }

    static void dfs(int node, List<List<Integer>> adj, boolean[] vis, Set<Integer> comp) {
        vis[node] = true;
        comp.add(node);
        for (int nbr : adj.get(node)) {
            if (!vis[nbr]) dfs(nbr, adj, vis, comp);
        }
    }

    static List<Set<Integer>> getComponents(int n, List<Edge> edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        for (Edge e : edges) {
            adj.get(e.u).add(e.v);
            adj.get(e.v).add(e.u);
        }

        boolean[] vis = new boolean[n];
        List<Set<Integer>> comps = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!vis[i]) {
                Set<Integer> comp = new HashSet<>();
                dfs(i, adj, vis, comp);
                comps.add(comp);
            }
        }
        return comps;
    }

    static Edge findReplacement(List<Edge> allEdges, List<Edge> currMST, List<Set<Integer>> comps) {
        if (comps.size() != 2) return null;

        Set<Integer> c1 = comps.get(0);
        Set<Integer> c2 = comps.get(1);

        Set<String> used = new HashSet<>();
        for (Edge e : currMST) {
            used.add(e.u + "," + e.v);
            used.add(e.v + "," + e.u);
        }

        Edge best = null;
        for (Edge e : allEdges) {
            if (used.contains(e.u + "," + e.v)) continue;

            if ((c1.contains(e.u) && c2.contains(e.v)) ||
                    (c2.contains(e.u) && c1.contains(e.v))) {
                if (best == null || e.w < best.w) {
                    best = e;
                }
            }
        }
        return best;
    }

    public static void main(String[] args) {
        int n = 5;
        List<Edge> edges = new ArrayList<>();

        edges.add(new Edge(0, 1, 2));
        edges.add(new Edge(0, 3, 6));
        edges.add(new Edge(1, 2, 3));
        edges.add(new Edge(1, 3, 8));
        edges.add(new Edge(1, 4, 5));
        edges.add(new Edge(2, 4, 7));
        edges.add(new Edge(3, 4, 9));

        System.out.println("Building MST.");
        List<Edge> mst = kruskal(n, edges);

        System.out.println("\nMST edges:");
        int total = 0;
        for (Edge e : mst) {
            System.out.println("  " + e);
            total += e.w;
        }
        System.out.println("Total weight: " + total);

        System.out.println("\nRemoving edge: " + mst.getFirst() + " ");

        List<Edge> remaining = new ArrayList<>();
        for (int i = 1; i < mst.size(); i++) {
            remaining.add(mst.get(i));
        }

        System.out.println("\nEdges after removal:");
        for (Edge e : remaining) {
            System.out.println("  " + e);
        }

        List<Set<Integer>> comps = getComponents(n, remaining);
        System.out.println("\nComponents formed: " + comps.size());
        for (int i = 0; i < comps.size(); i++) {
            System.out.println("  Component " + (i+1) + ": " + comps.get(i));
        }

        Edge replacement = findReplacement(edges, remaining, comps);

        if (replacement != null) {
            System.out.println("\nReplacement edge: " + replacement);
            remaining.add(replacement);

            System.out.println("\nNew MST:");
            int newTotal = 0;
            for (Edge e : remaining) {
                System.out.println("  " + e);
                newTotal += e.w;
            }
            System.out.println("New total weight: " + newTotal);
        } else {
            System.out.println("\nCouldn't find replacement edge");
        }
    }
}