/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.util;

/**
 *
 * @author Pedro, Gabi
 */

public class ParseUtil {
    public static Long toLong(String s) {
        try { return (s == null || s.isBlank()) ? null : Long.valueOf(s); }
        catch (Exception e) { return null; }
    }
    public static Integer toInt(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s); }
        catch (Exception e) { return null; }
    }
    public static boolean toBool(String s) {
        return "true".equalsIgnoreCase(s) || "on".equalsIgnoreCase(s) || "1".equals(s);
    }
}

