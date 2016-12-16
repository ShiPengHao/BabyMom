package com.yimeng.babymom.utils;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 计算血型工具
 */

public class BloodTypeUtils {

    private static final int TYPE_A_INDEX = 0;
    private static final int TYPE_B_INDEX = 1;
    private static final int TYPE_AB_INDEX = 2;
    private static final int TYPE_O_INDEX = 3;

    private static final String TYPE_A_String = "A型";
    private static final String TYPE_B_String = "B型";
    private static final String TYPE_AB_String = "AB型";
    private static final String TYPE_O_String = "O型";

    public static final int[] BLOOD_TYPES = new int[]{TYPE_A_INDEX, TYPE_B_INDEX, TYPE_AB_INDEX, TYPE_O_INDEX};
    public static final String[] BLOOD_TYPE_STRINGS = new String[]{TYPE_A_String, TYPE_B_String, TYPE_AB_String, TYPE_O_String};

    private static HashMap<Integer, String> typeAAChance = new HashMap<>();
    private static HashMap<Integer, String> typeABChance = new HashMap<>();
    private static HashMap<Integer, String> typeAOChance = new HashMap<>();
    private static HashMap<Integer, String> typeAABChance = new HashMap<>();
    private static HashMap<Integer, String> typeBBChance = new HashMap<>();
    private static HashMap<Integer, String> typeBOChance = new HashMap<>();
    private static HashMap<Integer, String> typeBABChance = new HashMap<>();
    private static HashMap<Integer, String> typeOOChance = new HashMap<>();
    private static HashMap<Integer, String> typeOABChance = new HashMap<>();
    private static HashMap<Integer, String> typeABABChance = new HashMap<>();

    static {
        DecimalFormat df = new DecimalFormat("0.00%");
        //AA
        typeAAChance.put(TYPE_A_INDEX, df.format(1.0 * 15 / 16));
        typeAAChance.put(TYPE_O_INDEX, df.format(1.0 * 1 / 16));
        //AB
        typeABChance.put(TYPE_A_INDEX, df.format(1.0 * 3 / 16));
        typeABChance.put(TYPE_B_INDEX, df.format(1.0 * 3 / 16));
        typeABChance.put(TYPE_AB_INDEX, df.format(1.0 * 9 / 16));
        typeABChance.put(TYPE_O_INDEX, df.format(1.0 * 1 / 16));
        //AO
        typeAOChance.put(TYPE_A_INDEX, df.format(1.0 * 12 / 16));
        typeAOChance.put(TYPE_O_INDEX, df.format(1.0 * 4 / 16));
        //A-AB
        typeAABChance.put(TYPE_A_INDEX, df.format(1.0 * 8 / 16));
        typeAABChance.put(TYPE_B_INDEX, df.format(1.0 * 2 / 16));
        typeAABChance.put(TYPE_AB_INDEX, df.format(1.0 * 6 / 16));
        //BB
        typeBBChance.put(TYPE_B_INDEX, df.format(1.0 * 15 / 16));
        typeBBChance.put(TYPE_O_INDEX, df.format(1.0 * 1 / 16));
        //B-AB
        typeBABChance.put(TYPE_A_INDEX, df.format(1.0 * 2 / 16));
        typeBABChance.put(TYPE_B_INDEX, df.format(1.0 * 8 / 16));
        typeBABChance.put(TYPE_AB_INDEX, df.format(1.0 * 6 / 16));
        //BO
        typeBOChance.put(TYPE_B_INDEX, df.format(1.0 * 12 / 16));
        typeBOChance.put(TYPE_O_INDEX, df.format(1.0 * 4 / 16));
        //OO
        typeOOChance.put(TYPE_O_INDEX, df.format(1));
        //O-AB
        typeOABChance.put(TYPE_A_INDEX, df.format(1.0 * 8 / 16));
        typeOABChance.put(TYPE_B_INDEX, df.format(1.0 * 8 / 16));
        //AB-AB
        typeABABChance.put(TYPE_A_INDEX, df.format(1.0 * 4 / 16));
        typeABABChance.put(TYPE_B_INDEX, df.format(1.0 * 4 / 16));
        typeABABChance.put(TYPE_AB_INDEX, df.format(1.0 * 8 / 16));

    }

    /**
     * 根据父母血型，计算子女血型的概率
     *
     * @param type1 一方血型
     * @param type2 另一方血型
     * @return 血型概率的键值对形式
     */
    public static HashMap<Integer, String> calculateBloodTypeChance(int type1, int type2) {
        switch (type1) {
            case TYPE_A_INDEX:
                switch (type2) {
                    case TYPE_A_INDEX:
                        return typeAAChance;
                    case TYPE_B_INDEX:
                        return typeABChance;
                    case TYPE_AB_INDEX:
                        return typeAABChance;
                    case TYPE_O_INDEX:
                        return typeAOChance;
                }
                break;
            case TYPE_B_INDEX:
                switch (type2) {
                    case TYPE_A_INDEX:
                        return typeABChance;
                    case TYPE_B_INDEX:
                        return typeBBChance;
                    case TYPE_AB_INDEX:
                        return typeBABChance;
                    case TYPE_O_INDEX:
                        return typeBOChance;
                }
                break;
            case TYPE_AB_INDEX:
                switch (type2) {
                    case TYPE_A_INDEX:
                        return typeAABChance;
                    case TYPE_B_INDEX:
                        return typeBABChance;
                    case TYPE_AB_INDEX:
                        return typeABABChance;
                    case TYPE_O_INDEX:
                        return typeOABChance;
                }
                break;
            case TYPE_O_INDEX:
                switch (type2) {
                    case TYPE_A_INDEX:
                        return typeAOChance;
                    case TYPE_B_INDEX:
                        return typeBOChance;
                    case TYPE_AB_INDEX:
                        return typeOABChance;
                    case TYPE_O_INDEX:
                        return typeOOChance;
                }
                break;
        }

        return null;
    }
}
