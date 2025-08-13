package com.fouristhenumber.utilitiesinexcess.mixins.transformers;

import java.util.Arrays;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformerIMerchant implements IClassTransformer {

    @Override
    public byte[] transform(String name, String tName, byte[] bytes) {
        if (bytes == null) return null;
        try {
            final ClassReader cr = new ClassReader(bytes);
            var is = cr.getInterfaces();
            if (is != null && is.length > 0
                && Arrays.asList(is)
                    .contains("net/minecraft/entity/IMerchant")) {
                final ClassNode cn = new ClassNode();
                cr.accept(cn, 0);
                MethodNode method = cn.methods.stream()
                    .filter(a -> a.name.equals("getRecipes"))
                    .findFirst()
                    .orElse(null);
                InsnList handle = new InsnList();
                handle.add(new InsnNode(Opcodes.DUP));// Dup
                handle.add(new VarInsnNode(Opcodes.ALOAD, 0));// Push this (instance of IMerchant)
                handle.add(new VarInsnNode(Opcodes.ALOAD, 1));// Push the player
                handle.add(
                    new MethodInsnNode(
                        0xb8,
                        "com/fouristhenumber/utilitiesinexcess/common/tileentities/TileEntityTradingPost",
                        "handleMerchant",
                        "(Lnet/minecraft/village/MerchantRecipeList;Lnet/minecraft/entity/IMerchant;Lnet/minecraft/entity/player/EntityPlayer;)V",
                        false));// invokestatic
                method.instructions.insertBefore(
                    method.instructions.getLast()
                        .getPrevious(),
                    handle);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
