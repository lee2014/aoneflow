package com.aoneai.workflow.manager.job.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class OTU {

    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", "echo $u");
        Map<String, String> env = pb.environment();
        // set environment variable u
        env.put("u", "util/");

        Process p = pb.start();
        String output = loadStream(p.getInputStream());
        String error = loadStream(p.getErrorStream());
        int rc = p.waitFor();
        System.out.println("Process ended with rc=" + rc);
        System.out.println("\nStandard Output:\n");
        System.out.println(output);
        System.out.println("\nStandard Error:\n");
        System.out.println(error);
    }

    private static String loadStream(InputStream s) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }

    @Test
    public void testProcessBuilder() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "echo Hello $name");
        Map<String, String> environment = processBuilder.environment();
        environment.put("name", "Alfredo Osorio");
        Process p = processBuilder.start();
        String line;
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }
        r.close();
        System.out.println("command " + processBuilder.command());
    }
}
