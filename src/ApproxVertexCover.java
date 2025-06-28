import java.util.*;

class Edge {
    int u, v, weight;

    Edge(int u, int v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
}

public class ApproxVertexCover {

    // בונה MST בעזרת קרוסקל
    public static List<Edge> buildMST(int n, List<Edge> edges) {
        List<Edge> mst = new ArrayList<>();
        edges.sort(Comparator.comparingInt(e -> e.weight));
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        for (Edge e : edges) {
            int pu = find(parent, e.u);
            int pv = find(parent, e.v);
            if (pu != pv) {
                mst.add(e);
                parent[pu] = pv;
            }
        }
        return mst;
    }

    // פונקציית עזר לאיחוד קבוצות (Union-Find)
    public static int find(int[] parent, int u) {
        if (parent[u] != u)
            parent[u] = find(parent, parent[u]);
        return parent[u];
    }

    // מחזירה קודקודים בעלי דרגה אי-זוגית ב־MST
    public static List<Integer> getOddDegreeVertices(List<Edge> mst, int n) {
        int[] degree = new int[n];
        for (Edge e : mst) {
            degree[e.u]++;
            degree[e.v]++;
        }
        List<Integer> odds = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] % 2 == 1)
                odds.add(i);
        }
        return odds;
    }

    // מוצא התאמה בזוגות (matching) הכי זולה לקודקודים האי-זוגיים
    public static List<Edge> minWeightMatching(List<Integer> oddVertices, int[][] graph) {
        List<Edge> matching = new ArrayList<>();
        boolean[] used = new boolean[graph.length];

        for (int i = 0; i < oddVertices.size(); i++) {
            if (used[oddVertices.get(i)]) continue;
            int u = oddVertices.get(i);
            int minWeight = Integer.MAX_VALUE;
            int matchV = -1;
            for (int j = i + 1; j < oddVertices.size(); j++) {
                int v = oddVertices.get(j);
                if (!used[v] && graph[u][v] < minWeight) {
                    minWeight = graph[u][v];
                    matchV = v;
                }
            }
            if (matchV != -1) {
                matching.add(new Edge(u, matchV, graph[u][matchV]));
                used[u] = true;
                used[matchV] = true;
            }
        }
        return matching;
    }

    public static void main(String[] args) {
        int n = 4;
        int[][] graph = new int[n][n];
        graph[0][1] = graph[1][0] = 3;
        graph[0][2] = graph[2][0] = 5;
        graph[1][3] = graph[3][1] = 4;
        graph[2][3] = graph[3][2] = 2;

        List<Edge> allEdges = new ArrayList<>();
        allEdges.add(new Edge(0, 1, 3));
        allEdges.add(new Edge(0, 2, 5));
        allEdges.add(new Edge(1, 3, 4));
        allEdges.add(new Edge(2, 3, 2));

        // בניית עץ MST
        List<Edge> mst = buildMST(n, allEdges);
        System.out.println("MST:");
        for (Edge e : mst)
            System.out.println(e.u + " - " + e.v + " : " + e.weight);

        // מציאת קודקודים אי-זוגיים
        List<Integer> oddVertices = getOddDegreeVertices(mst, n);
        System.out.println("Odd-degree vertices: " + oddVertices);

        // חיפוש Matching מינימלי
        List<Edge> matching = minWeightMatching(oddVertices, graph);
        System.out.println("Matching:");
        for (Edge e : matching)
            System.out.println(e.u + " - " + e.v + " : " + e.weight);

        // חיבור ל־MST
        mst.addAll(matching);
        System.out.println("Final Graph (MST + Matching):");
        for (Edge e : mst)
            System.out.println(e.u + " - " + e.v + " : " + e.weight);
    }
}
