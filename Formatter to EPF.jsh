import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.StandardOpenOption;

void checkFormat(List<String> lines) {
	int size = lines.size();
	
	assert lines.get(0).startsWith("<?xml") : lines.get(0);
	assert lines.get(1).startsWith("<profiles") : lines.get(1);
	assert lines.get(2).trim().startsWith("<profile") : lines.get(2);
	List<String> settingsLines = lines.subList(3, size - 2);
	List<String> notSettings = settingsLines.stream().filter(s -> !s.trim().startsWith("<setting id=\"org.eclipse.jdt.core.")).collect(Collectors.toList());
	assert notSettings.isEmpty() : "Not settings: " + notSettings;
	assert lines.get(size - 2).trim().equals("</profile>") : lines.get(size - 2);
	assert lines.get(size - 1).trim().equals("</profiles>") : lines.get(size - 1);
}

void exportFormatter(Path input, Path output) throws IOException {
	List<String> lines = Files.readAllLines(input);
	checkFormat(lines);
	
	int size = lines.size();
	List<String> settingsLines = lines.subList(3, size - 2);
	Collections.sort(settingsLines);

	List<String> trimmed = lines.stream().map(String::trim).collect(Collectors.toList());
	
	String key = "/instance/org.eclipse.jdt.ui/org.eclipse.jdt.ui.formatterprofiles";
	String value = trimmed.stream().map(s -> s.replace("=", "\\=")).collect(Collectors.joining("\\n"));
	Files.writeString(output, key + "=" + value + "\\n", StandardOpenOption.CREATE_NEW);
}

void main() throws Exception {
	String inputFile = System.getProperty("input", "Untitled.xml");
	Path inputPath = Path.of(inputFile);
	Path outputPath = Path.of("out.epf");
	exportFormatter(inputPath, outputPath);
	System.out.println("Exported " + inputPath + ".");
}

main();

