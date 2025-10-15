package com.docs.git.model;

public class Version {
    int major;
    int minor;
    int patch;

    // Construtor
    public Version (int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    // Exemplo de uso 0.0.0 -> 1.0.0
    public void addMajor() {
        this.major++;
        this.minor = 0;
        this.patch = 0;
    }

    // Exemplo de uso 0.0.0 -> 0.1.0
    public void addMinor() {
        this.minor++;
        this.patch = 0;
    }

    // Exemplo de uso 0.0.0 -> 0.0.1
    public void addPatch() {
        this.patch++;
    }

    public int[] toArray() {
        return new int[]{major, minor, patch};
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}