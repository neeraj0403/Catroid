/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.ist.tugraz.catroid.test.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import at.tugraz.ist.catroid.utils.UtilFile;

public class LicenseTest extends TestCase {
	private static final String[] DIRECTORIES = { ".", "../catroid", "../catroidTest", "../catroidUiTest", };

	private static final String[] AGPL_FILES = {
			"..\\catroid\\src\\at\\tugraz\\ist\\catroid\\stage\\NativeAppActivity.java",
			"..\\catroidTest\\res\\raw\\test_project.xml",
			"..\\catroidTest\\src\\at\\tugraz\\ist\\catroid\\nativetest\\content\\brick\\SetCostumeBrickTest.java",
			"..\\catroidTest\\src\\at\\tugraz\\ist\\catroid\\nativetest\\content\\sprite\\CostumeTest.java",
			"..\\catroidTest\\src\\at\\tugraz\\ist\\catroid\\nativetest\\content\\sprite\\SoundManagerTest.java",
			"..\\catroidTest\\src\\at\\tugraz\\ist\\catroid\\nativetest\\io\\StorageHandlerTest.java" };

	private ArrayList<String> gplLicenseText;
	private ArrayList<String> agplLicenseText;
	private boolean allLicenseTextsPresentAndCorrect;
	private StringBuilder errorMessages;

	public LicenseTest() throws IOException {
		allLicenseTextsPresentAndCorrect = true;
		errorMessages = new StringBuilder();
		gplLicenseText = readLicenseFile(new File("res/gpl_license_text.txt"));
		agplLicenseText = readLicenseFile(new File("res/agpl_license_text.txt"));
	}

	private ArrayList<String> readLicenseFile(File licenseTextFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(licenseTextFile));
		String line = null;
		ArrayList<String> licenseText = new ArrayList<String>();
		while ((line = reader.readLine()) != null) {
			if (line.length() > 0) {
				licenseText.add(line);
			}
		}
		return licenseText;
	}

	private void checkFileForLicense(File file, ArrayList<String> licenseText) throws IOException {
		StringBuilder fileContentsBuilder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = reader.readLine()) != null) {
			fileContentsBuilder.append(line);
		}

		final String fileContents = fileContentsBuilder.toString();

		int lastPosition = 0;
		boolean notFound = false;
		boolean wrongOrder = false;
		for (String licenseTextLine : licenseText) {
			int position = fileContents.indexOf(licenseTextLine);
			if (position == -1) {
				notFound = true;
			} else if (position <= lastPosition) {
				wrongOrder = true;
			}

			lastPosition = position;
		}

		if (notFound) {
			allLicenseTextsPresentAndCorrect = false;
			errorMessages.append("License text was not found in file " + file.getCanonicalPath() + "\n");
		} else if (wrongOrder) {
			allLicenseTextsPresentAndCorrect = false;
			errorMessages.append("License text was found in the wrong order in file " + file.getCanonicalPath() + "\n");
		}
	}

	public void testLicensePresentInAllFiles() throws IOException {
		for (String directoryName : DIRECTORIES) {
			File directory = new File(directoryName);
			assertTrue("Couldn't find directory: " + directoryName, directory.exists() && directory.isDirectory());
			assertTrue("Couldn't read directory: " + directoryName, directory.canRead());

			List<File> filesToCheck = UtilFile.getFilesFromDirectoryByExtension(directory, new String[] { ".java",
					".xml" });
			for (File file : filesToCheck) {
				if (isAgplFile(file.getPath())) {
					checkFileForLicense(file, agplLicenseText);
				} else {
					checkFileForLicense(file, gplLicenseText);
				}
			}
		}
		assertTrue("Correct license text was not found in all files:\n" + errorMessages.toString(),
				allLicenseTextsPresentAndCorrect);
	}

	private boolean isAgplFile(String file) {
		for (String agplFile : AGPL_FILES) {
			if (file.equals(agplFile)) {
				return true;
			}
		}
		return false;
	}
}
