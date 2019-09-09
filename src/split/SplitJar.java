import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SplitJar {
	public static void main(String[] args) {
		System.out.println("Starting");
		File input = new File(args[0]);
		String baseName = input.getName().substring(0, input.getName().length() - 4);
		File config = new File(args[1]);
		File outFolder = new File(args[2]);
		File tmp = new File(".tmp");
		File uz = new File(tmp, ".uz");
		Map<String, List<String>> files = new HashMap<>();
		uz.mkdirs();
		try {
			decompressZip(input, uz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String name = null;
		System.out.println("Reading Config");
		try (BufferedReader rd = new BufferedReader(new FileReader(config))){
			String ln;
			while((ln = rd.readLine()) != null){
				if(ln.startsWith("$")){
					name = ln.substring(1);
					files.computeIfAbsent(name, k -> new ArrayList<>());
				}else{
					files.get(name).add(ln);
				}
			}
		} catch (IOException e) {
		}
		System.out.println("Splitting jars");
		for (Entry<String, List<String>> e : files.entrySet()) {
			System.out.println("Jar name: " + e.getKey());
			File f = new File(tmp, e.getKey());
			f.mkdirs();
			File out = new File(outFolder, baseName + "_" + e.getKey() + ".jar");
			List<Pattern> patterns = e.getValue().stream().map(Pattern::compile).collect(Collectors.toList());
			URI base = uz.toURI();
			walkFiles(uz, in -> {
				String relPath = base.relativize(in.toURI()).getPath();
				System.out.println("Checking: " + relPath);
				if(patterns.stream().map(p -> p.matcher(relPath)).anyMatch(Matcher::matches)){
					if(!in.isDirectory()){
						System.out.println("Copying: " + relPath);
						try {
							File fo = new File(f, relPath);
							fo.getParentFile().mkdirs();
							copy(in, fo);
						} catch (IOException e1) {
						}
					}
				}
			});
			System.out.println("Creating Jar: " + out);
			try (FileOutputStream os = new FileOutputStream(out)){
				zip(f, os);
			} catch (IOException e2) {
			}
		}
		System.out.println("Cleaning up");
		recDel(tmp);
		tmp.delete();
	}
	public static void decompressZip(File in, File out) throws IOException {
		System.out.println("Start unzipping " + in.getAbsolutePath());
		byte[] buffer = new byte[1024];
		ZipFile zip = new ZipFile(in);
		int size = zip.size();
		int i = 0;
		zip.close();
		ZipInputStream zis = new ZipInputStream(new FileInputStream(in));

		ZipEntry ze = zis.getNextEntry();
		while(ze != null){
			String fileName = ze.getName();
			File newFile = new File(out, fileName);

			System.out.println("Unzipping: "+ newFile.getAbsoluteFile());
			newFile.getParentFile().mkdirs();

			if(ze.isDirectory()){
				newFile.mkdirs();
			}else{
				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			ze = zis.getNextEntry();
			i++;
		}

		zis.closeEntry();
		zis.close();

		System.out.println("Unzipped " + in.getAbsolutePath());
	}
	public static void recDel(File dir){
		File[] f = dir.listFiles();
		if(f != null){
			for (int i = 0;i < f.length;i++) {
				File file = f[i];
				if(file.isDirectory()){
					recDel(file);
				}
				file.delete();
			}
		}
	}
	private static void zip(File directory, OutputStream os) throws IOException
	{
		URI base = directory.toURI();
		Deque<File> queue = new LinkedList<>();
		queue.push(directory);
		Closeable res = null;
		try
		{
			ZipOutputStream zout = new ZipOutputStream(os);
			res = zout;
			while (!queue.isEmpty())
			{
				directory = queue.pop();
				for (File kid : directory.listFiles())
				{
					String name = base.relativize(kid.toURI()).getPath();
					String nameF = name.endsWith("/") ? name.substring(0, name.length() - 1) : name;
					//System.out.println(nameF);
					if (kid.isDirectory())
					{
						queue.push(kid);
						name = name.endsWith("/") ? name : name + "/";
						zout.putNextEntry(new ZipEntry(name));
					} else {
						System.out.println("Added: " + nameF);
						zout.putNextEntry(new ZipEntry(name));
						copy(kid, zout);
						zout.closeEntry();
					}
				}
			}
		} finally
		{
			res.close();
		}
	}
	private static void copy(File kid, OutputStream zout) throws IOException {
		try(FileInputStream fin = new FileInputStream(kid)){
			copy(fin, zout);
		}
	}
	private static void copy(File kid, File out) throws IOException {
		try(FileInputStream fin = new FileInputStream(kid);FileOutputStream fout = new FileOutputStream(out)){
			copy(fin, fout);
		}
	}
	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] d = new byte[1024];
		int len = 0;
		while((len = is.read(d, 0, d.length)) > 0){
			os.write(d, 0, len);
		}
	}
	private static void walkFiles(File folder, Consumer<File> fileConsumer){
		for (File f : folder.listFiles()) {
			if(f.isDirectory()){
				walkFiles(f, fileConsumer);
			}else{
				fileConsumer.accept(f);
			}
		}
	}
}
