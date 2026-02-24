package com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart;

import codechicken.lib.lighting.LC;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.Vertex5;
import codechicken.lib.render.uv.UV;

import static com.fouristhenumber.utilitiesinexcess.common.renderers.Multipart.SphereRenderingHelper.WrapNumber;

public class SphereFace implements CCRenderState.IVertexSource
{
    public Vertex5[] loadedVert = new Vertex5[4];
    public LC[] lightCoords = new LC[] { new LC(), new LC(), new LC(), new LC()};
    public boolean lcComputed = false;

    @Override
    public Vertex5[] getVertices() {
        return loadedVert;
    }

    @Override
    public <T> T getAttributes(CCRenderState.VertexAttribute<T> attr) {
        return attr == CCRenderState.lightCoordAttrib() && lcComputed ? (T) lightCoords : null;
    }

    @Override
    public boolean hasAttribute(CCRenderState.VertexAttribute<?> attr) {
        return attr == CCRenderState.sideAttrib() || attr == CCRenderState.lightCoordAttrib() && lcComputed;
    }

    @Override
    public void prepareVertex(CCRenderState state)
    {

    }

    // Latitude and longitude here are the face coordinates not vertex coordinates
    public void loadSphericFace(int latitude, int longitude)
    {
        double u1;
        double u2;
        double v1;
        double v2;

        // wrap lats
        int lon0 = longitude;
        int lon1 = longitude + 1;
        int lat0 = latitude;
        int lat1 = WrapNumber(0, 15, latitude + 1);

        // middle band
        if (longitude < 6 && longitude > 1)
        {
            int side = 0;
            int offset = latitude % 4;
            u1 = 0;
            v1 = 0;
            u2 = 1;
            v2 = 1;

            // Bottom-left
            loadedVert[0] = new Vertex5(
                SphereRenderingHelper.verts[lon0][lat0],
                new UV(u1, v1, side)
            );

            // Top-left
            loadedVert[1] = new Vertex5(
                SphereRenderingHelper.verts[lon0][lat1],
                new UV(u1, v2, side)
            );

            // Top-right
            loadedVert[2] = new Vertex5(
                SphereRenderingHelper.verts[lon1][lat1],
                new UV(u2, v2, side)
            );

            // Bottom-right
            loadedVert[3] = new Vertex5(
                SphereRenderingHelper.verts[lon1][lat0],
                new UV(u2, v1, side)
            );

        }
        else // Top and bottom bands
        {

        }
    }
}
