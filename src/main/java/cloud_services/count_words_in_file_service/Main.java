/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloud_services.count_words_in_file_service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@SpringBootApplication
public class Main {
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
    
    @GetMapping("/")
    public String index() {
        try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                
            }
        return "index";
    }
    
    @GetMapping("/long")
    public String longIndex() {
        try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) {
                
            }
        return "index";
    }
    
    final String UPLOADED_FILES_DIR_NAME = "UploadedFiles";
    
    @PostMapping("/fileUpload")
    public String uploadFile(@RequestParam("var1") MultipartFile file1, @RequestParam("var2") MultipartFile file2) {
         
        File folder = new File(UPLOADED_FILES_DIR_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String filename1=file1.getOriginalFilename();        
        String filename2=file2.getOriginalFilename();        
        try {
            byte fileBytes1[]=file1.getBytes();
            byte fileBytes2[]=file2.getBytes();
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+filename1))) {
                bos.write(fileBytes1);
                bos.flush();
            }
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+filename2))) {
                bos.write(fileBytes2);
                bos.flush();
            } 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "index";
    }
    
    @PostMapping("/fileUploadFileAndArray")
    public String uploadFileAndArray(@RequestParam("var1") MultipartFile file1, @RequestParam("var2") MultipartFile[] fileArray) {
         
        File folder = new File(UPLOADED_FILES_DIR_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String filename1=file1.getOriginalFilename();    
        try {
            byte fileBytes1[]=file1.getBytes();
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+filename1))) {
                bos.write(fileBytes1);
                bos.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(MultipartFile file : fileArray) {
            String filename=file.getOriginalFilename();
            try {
                byte fileBytes[]=file.getBytes();
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+filename))) {
                    bos.write(fileBytes);
                    bos.flush();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
        
        return "index";
    }
    
    @PostMapping("/countWords")
    @ResponseBody
    public FileSystemResource countWordsInFile(@RequestParam("inputFile") MultipartFile inputFile) {
        TreeMap<String, Integer> wordsAndTheirAmounts = new TreeMap<>();
        
        File folder = new File(UPLOADED_FILES_DIR_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        String fileName=inputFile.getOriginalFilename();    
        try {
            byte fileBytes[]=inputFile.getBytes();
            
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+fileName))) {
                bos.write(fileBytes);
                bos.flush();
            }
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(UPLOADED_FILES_DIR_NAME+"/"+fileName))){
                String line = bufferedReader.readLine();
                while(line != null) {
                    String[] splited = line.split(" ");
                    for(String word : splited) {
                        if(wordsAndTheirAmounts.containsKey(word))
                            wordsAndTheirAmounts.put(word, wordsAndTheirAmounts.get(word) + 1);
                        else
                            wordsAndTheirAmounts.put(word, 1);
                    }

                    line = bufferedReader.readLine();
                }
            }
            try(FileWriter fileWriter = new FileWriter(UPLOADED_FILES_DIR_NAME+"/output" + fileName)){
                for(String word : wordsAndTheirAmounts.keySet()) {
                    fileWriter.write(word + " " + wordsAndTheirAmounts.get(word) + "\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new FileSystemResource(UPLOADED_FILES_DIR_NAME+"/output" + fileName);
    }
    
    @PostMapping("/countWordsMultipleSorted")
    @ResponseBody
    public FileSystemResource countWordsInFileMultipleSorted(@RequestParam("inputFiles") MultipartFile[] inputFiles) {
        TreeMap<String, Integer> wordsAndTheirAmounts = new TreeMap<>();
        
        File folder = new File(UPLOADED_FILES_DIR_NAME);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            for(MultipartFile file : inputFiles) {
                String fileName=file.getOriginalFilename();
            
                byte fileBytes[]=file.getBytes();
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(UPLOADED_FILES_DIR_NAME+"/"+fileName))) {
                    bos.write(fileBytes);
                    bos.flush();
                }
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(UPLOADED_FILES_DIR_NAME+"/"+fileName))){
                    String line = bufferedReader.readLine();
                    while(line != null) {
                        String[] splited = line.split(" ");
                        if(wordsAndTheirAmounts.containsKey(splited[0]))
                            wordsAndTheirAmounts.put(splited[0], wordsAndTheirAmounts.get(splited[0]) + Integer.parseInt(splited[1]));
                        else
                            wordsAndTheirAmounts.put(splited[0], Integer.parseInt(splited[1]));

                        line = bufferedReader.readLine();
                    }
                } 
            }
            try(FileWriter fileWriter = new FileWriter(UPLOADED_FILES_DIR_NAME+"/output2")){
                for(String word : wordsAndTheirAmounts.keySet()) {
                    fileWriter.write(word + " " + wordsAndTheirAmounts.get(word) + "\n");
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new FileSystemResource(UPLOADED_FILES_DIR_NAME+"/output2");
    }
}
