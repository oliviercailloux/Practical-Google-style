import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Collections;
import java.util.stream.Collectors;

void sort(Path path) throws IOException {
	var lines = Files.readAllLines(path);
	int size = lines.size();

	assert lines.get(0).startsWith("<?xml") : lines.get(0);
	assert lines.get(1).startsWith("<profiles") : lines.get(1);
	assert lines.get(2).trim().startsWith("<profile") : lines.get(2);
	var settingsLines = lines.subList(3, size - 2);
	assert settingsLines.stream().allMatch(s -> s.trim().startsWith("<setting id=\"org.eclipse.jdt.core."));
	assert lines.get(size - 2).trim().equals("</profile>") : lines.get(size - 2);
	assert lines.get(size - 1).trim().equals("</profiles>") : lines.get(size - 1);

	Collections.sort(settingsLines);

	var trimmed = lines.stream().map(String::trim).collect(Collectors.toList());
	Files.write(path, trimmed);
	
	System.out.println("Sorted " + path + ".");
}

void main() throws Exception {
	String file = System.getProperty("file", "Untitled.xml");
	Path path = Path.of(file);
	sort(path);
}

main();

