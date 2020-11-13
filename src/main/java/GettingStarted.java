import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public class GettingStarted {
	
	public static ArrayList<String> words;
	public static ArrayList<String> wordsDefinitions;
	public static ArrayList<String> wordsOut;
	
	public static ArrayList<Path> sourcePathList;
	public static ArrayList<Path> targetPathList;
	
	public static String downloadPath;
	public static String inputFilePath;
	public static String outputFilePath;
	
	public static String inputFileName;
	
	public static void main(String[] args) throws InterruptedException, AWTException, UnsupportedFlavorException, IOException {
		//testGoogleSearch();
		
		sourcePathList = new ArrayList<Path>();
		targetPathList = new ArrayList<Path>();
		
		words = new ArrayList<String>();
		wordsDefinitions = new ArrayList<String>();
		wordsOut = new ArrayList<String>();
		initUI();
		readInput();
		downloadRussianWords();
		createSummaryFile();
		renameAudioFiles();
	}
	
	public static void chooseInputFilePath() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose your file input text file: ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile() && jfc.getSelectedFile().getName().endsWith(".txt")) {
                System.out.println("You selected the following input file: " + jfc.getSelectedFile());
                inputFilePath = jfc.getSelectedFile().toString();
                inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("\\"), inputFilePath.length()-4);
            } else {
            	JOptionPane.showMessageDialog(new JFrame(), "The choosen file is not a text file, it has to be type of: .txt\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            	System.exit(0);
            }
        } else {
        	JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a file\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
        	System.exit(0);
        }        
	}
	
	public static void chooseOutputFilePath() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose a directory to save your files (the audio files and the smart text file output): ");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isDirectory()) {
                System.out.println("You selected the following output directory: " + jfc.getSelectedFile());
                downloadPath = jfc.getSelectedFile().toString() + "\\";                
                outputFilePath = jfc.getSelectedFile().toString() + inputFileName + "_out.txt";
                System.out.println("Your output file will be called: " + outputFilePath);
            } else {
            	JOptionPane.showMessageDialog(new JFrame(), "You have to choose a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
            	System.exit(0);
            }
        } else {
        	JOptionPane.showMessageDialog(new JFrame(), "You haven't choosen a directory\nPlease restart the program and try again!", "", JOptionPane.WARNING_MESSAGE);
        	System.exit(0);
        }  
	}
	
	public static void initUI() {
		
		chooseInputFilePath();
		
		chooseOutputFilePath();
	}
	
	public static void renameAudioFiles() {
		try{
			for(int i = 0; i < sourcePathList.size(); i++) {
				Files.move(sourcePathList.get(i), targetPathList.get(i));
			}
		  } catch (IOException e) {
		    e.printStackTrace();
		  }
	}
	
	public static void createSummaryFile() {
		try {
			FileOutputStream file;
			if(outputFilePath == null || outputFilePath == "") {
				file = new FileOutputStream("C:/Users/Benedek/Desktop/_test_4_out.txt");
			} else {
				file = new FileOutputStream(outputFilePath);
			}
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file, Charset.forName("UTF-8"));
			BufferedWriter bw = new BufferedWriter(outputStreamWriter);
			
			String downloadsDirPath;
			String home = System.getProperty("user.home");
			downloadsDirPath = home+"/Downloads/"; 
			
			if(downloadPath == null || downloadPath == "") {
				downloadPath = "c:\\Users\\Benedek\\Downloads\\";
			}
			String fullFilePathOld;
			String fullFilePathNew;
			Path source;
			Path target;
			String line, line2, theNewFileName;
			for(int i = 0; i < words.size(); i++) {
				line = words.get(i);
				line2 = wordsDefinitions.get(i);
				theNewFileName = line.replace(" ", "-");		
				fullFilePathOld = downloadsDirPath + line + ".mp3";
				fullFilePathNew = downloadPath + theNewFileName + ".mp3";
				
				source = Paths.get(fullFilePathOld.replace("\\", "/"));
				target = Paths.get(fullFilePathNew.replace("\\", "/"));
				
				sourcePathList.add(source);
				targetPathList.add(target);
				
				wordsOut.add(line + "[sound:" + fullFilePathNew + "];" + line2 + "\n");				
				bw.write(wordsOut.get(i));
			}
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < wordsOut.size(); i++) {
			System.out.println(wordsOut.get(i));
		}
	}
	
	public static void readInput() {
		try {
		FileInputStream file;
		if(inputFilePath == null || inputFilePath == "") {
			file = new FileInputStream("C:/Users/Benedek/Desktop/_test_4.txt");
		} else {
			file = new FileInputStream(inputFilePath);
		}
		InputStreamReader inputStreamReader = new InputStreamReader(file, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(inputStreamReader);
		String line = br.readLine();
		String currentWord;
		String currentDefinition;
		//Path currentPath;
		while(line != null) {
			//System.out.println(line);
			currentWord = line.substring(0, line.indexOf(","));
			currentDefinition = line.substring(line.indexOf(",")+1, line.length());
			System.out.println(currentDefinition);
			//currentPath = Paths.get(line.substring(line.indexOf("[")+7, line.indexOf("]")));
			byte bytes[] = currentWord.getBytes("UTF-8"); 
			String value = new String(bytes, "UTF-8"); 
			System.out.println(value);
			//System.out.println(currentPath.toString());
			words.add(currentWord);
			wordsDefinitions.add(currentDefinition);
			//pathList.add(currentPath);
			line = br.readLine();
		}
		br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
  
  public static void downloadRussianWords() throws InterruptedException, AWTException, UnsupportedFlavorException, IOException {
	 System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
	 
	 WebDriver driver = new ChromeDriver();
	 driver.get("https://hearling.com/clips");
	 //WebElement emailField = driver.findElement(By.name("email"));
	 // toth.balint.benedek@gmail.com
	 WebElement username = driver.findElement(By.name("email"));
	 //((JavascriptExecutor)driver).executeAsyncScript("arguments[0].value='toth.balint.benedek@gmail.com'",username);
	 String usernameOfMineReal = "tothgy74@gmail.com";
	 StringSelection stringSelection = new StringSelection(usernameOfMineReal);
	 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	 
	 String originalClipboardContent = "";
	 StringSelection stringSelectionOriginal = new StringSelection(originalClipboardContent);
	 
	 try {
		 originalClipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);
		 stringSelectionOriginal = new StringSelection(originalClipboardContent);
	 } catch(Exception e) {}
	 
	 clipboard.setContents(stringSelection, stringSelection);
	 username.click();
	 
	 Robot robot = new Robot();
	 robot.keyPress(KeyEvent.VK_CONTROL);
	 robot.keyPress(KeyEvent.VK_V);
	 robot.keyRelease(KeyEvent.VK_V);
	 robot.keyRelease(KeyEvent.VK_CONTROL);
	 
	 Thread.sleep(1000);
	 clipboard.setContents(stringSelectionOriginal, stringSelectionOriginal);

	 WebElement passwordField = ((WebDriver)driver).findElement(By.name("password"));
	 Thread.sleep(3000);
	 passwordField.sendKeys("Zvs5BYdbqF4pGg3");
	 Thread.sleep(5000);
	 WebElement loginButton = driver.findElement(By.className("button--stretch"));
	 loginButton.click();
	 Thread.sleep(2000);
	 
	 for(int i = 0; i < words.size(); i++) {
		 WebElement newClipButton = driver.findElement(By.className("button"));
		 newClipButton.click();
		 Thread.sleep(2000);
		 if(i == 0) {
			 WebElement russianButton = driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/section/div/ul/li[25]"));
			 russianButton.click();
			 Thread.sleep(2000);
			 WebElement theCorrectVoice = driver.findElement(By.xpath("/html/body/div/div/main/section/div[3]/ul/li[8]"));
			 theCorrectVoice.click();
			 Thread.sleep(2000);
		 }
		 WebElement nextButton = driver.findElement(By.xpath("/html/body/div/div/main/section/div[4]/button"));
		 nextButton.click();
		 Thread.sleep(2000);
		 WebElement textInputField = driver.findElement(By.xpath("/html/body/div/div/main/div/div[2]/div/div[2]/textarea"));
		 textInputField.sendKeys(words.get(i));
		 Thread.sleep(2000);
		 WebElement generateVoiceButton = driver.findElement(By.xpath("/html/body/div/div/main/div/div[1]/section/div[3]/button"));
		 generateVoiceButton.click();
		 Thread.sleep(4000);
		 WebElement downloadButton = driver.findElement(By.xpath("/html/body/div/div/main/section/div[1]/ul/li/div/div[3]/a"));
		 downloadButton.click();
		 Thread.sleep(2000);
		 WebElement mainPageButton = driver.findElement(By.xpath("/html/body/div/div/header/div/a"));
		 mainPageButton.click();
		 Thread.sleep(2000);
	 }
	 
	 Thread.sleep(15000);
	 driver.quit();
  }
}