package com.fouristhenumber.utilitiesinexcess.compat.simpleskinbackport;

import com.fouristhenumber.utilitiesinexcess.common.renderers.GloveRenderer;

import roadhog360.simpleskinbackport.ducks.IArmsState;

public class SsbCompat {

    public static void init() {
        GloveRenderer.getTopCube = (player) -> {
            if (((IArmsState) player).ssb$isSlim()) {
                return GloveRenderer.topCubeSlim;
            }
            return GloveRenderer.topCube;
        };
        GloveRenderer.getBottomCube = (player) -> {
            if (((IArmsState) player).ssb$isSlim()) {
                return GloveRenderer.bottomCubeSlim;
            }
            return GloveRenderer.bottomCube;
        };
    }
}
