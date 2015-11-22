package blue.heldplayer.mods.aurora.client.render;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class HemisphereRenderer {

    private float[][][] triangles;

    public HemisphereRenderer(float radius, int breakdown) {
        int triangleCount = 4;
        List<float[][]> oldTriangles, newTriangles;
        newTriangles = new ArrayList<float[][]>();
        newTriangles.add(new float[][] { new float[] { 0.0F, radius, 0.0F }, new float[] { 0.0F, 0.0F, radius }, new float[] { radius, 0.0F, 0.0F } });
        newTriangles.add(new float[][] { new float[] { 0.0F, radius, 0.0F }, new float[] { radius, 0.0F, 0.0F }, new float[] { 0.0F, 0.0F, -radius } });
        newTriangles.add(new float[][] { new float[] { 0.0F, radius, 0.0F }, new float[] { 0.0F, 0.0F, -radius }, new float[] { -radius, 0.0F, 0.0F } });
        newTriangles.add(new float[][] { new float[] { 0.0F, radius, 0.0F }, new float[] { -radius, 0.0F, 0.0F }, new float[] { 0.0F, 0.0F, radius } });
        for (int i = 0; i < breakdown; i++) {
            oldTriangles = newTriangles;
            newTriangles = new ArrayList<float[][]>();
            for (float[][] triangle : oldTriangles) {
                float[][] mids = new float[3][];
                for (int j = 0; j < 3; j++) {
                    int k = (j + 1) % 3;
                    mids[j] = new float[] { (triangle[j][0] + triangle[k][0]) / 2.0F, (triangle[j][1] + triangle[k][1]) / 2.0F, (triangle[j][2] + triangle[k][2]) / 2.0F };
                }
                newTriangles.add(mids);
                for (int j = 0; j < 3; j++) {
                    newTriangles.add(new float[][] { triangle[j], mids[j], mids[(j + 2) % 3] });
                }
            }
        }
        this.triangles = newTriangles.toArray(new float[newTriangles.size()][][]);
        for (float[][] triangle : this.triangles) {
            for (float[] vertex : triangle) {
                this.normalize(vertex, radius);
            }
        }
    }

    private void normalize(float[] vertex, float radius) {
        float dx = vertex[0];
        float dy = vertex[1];
        float dz = vertex[2];
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        vertex[0] *= radius / distance;
        vertex[1] *= radius / distance;
        vertex[2] *= radius / distance;
    }

    public void render() {
        GL11.glBegin(GL11.GL_TRIANGLES);
        for (float[][] triangle : this.triangles) {
            for (int i = 0; i < 3; i++) {
                GL11.glVertex3d(triangle[i][0], triangle[i][1], triangle[i][2]);
            }
        }
        GL11.glEnd();
    }
}
