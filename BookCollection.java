/**
 * The given code is provided to assist you to complete the required tasks. But the 
 * given code is often incomplete. You have to read and understand the given code 
 * carefully, before you can apply the code properly. You might need to implement 
 * additional procedures, such as error checking and handling, in order to apply the 
 * code properly.
 */
 
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

// you need to import some xml libraries

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// import any standard library if needed

/**
 * A book collection holds 0 or more books in a collection.
 */
public class BookCollection {
	private List<Book> books;

	/**
	 * Creates a new collection with no books by default.
	 */
	public BookCollection() {
		this.books = new ArrayList<Book>();
	}

	/**
	 * Creates a new book collection with the specified list of books pre-defined.
	 *
	 * @param books A books list.
	 */
	public BookCollection(List<Book> books) {
		this.books = books;
	}

	/**
	 * Returns the current list of books stored by this collection.
	 *
	 * @return A (mutable) list of books.
	 */
	public List<Book> getList() {
		return books;
	}

	/**
	 * Sets the list of books in this collection to the specified value.
	 */
	public void setList(List<Book> books) {
		this.books = books;
	}

	/**
	 * A simple human-readable toString implementation. Not particularly useful to
	 * save to disk.
	 *
	 * @return A human-readable string for printing
	 */
	@Override
	public String toString() {
		return this.books.stream().map(book -> " - " + book.display() + "\n").collect(Collectors.joining());
	}

	/**
	 * Saves this collection to the specified "bespoke" file.
	 *
	 * @param file The path to a file.
	 */
	public void saveToBespokeFile(File file) {
		// TODO: Implement this function yourself. The specific hierarchy is up to you,
		// but it must be in a bespoke format and should match the
		// load function.
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));) {
			for (int i = 0; i < books.size(); i++) {      //上面写了book是个list
				bufferedWriter.write(books.get(i).title + ";" + books.get(i).authorName + ";" + books.get(i).yearReleased + ";" + books.get(i).bookGenre + "\n");
			}                       //创建的时候每个个例用属性读取 直接读取属性就有内容
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Saves this collection to the specified JSON file.
	 *
	 * @param file The path to a file.
	 */
	public void saveToJSONFile(File file) {
		// TODO: Implement this function yourself. The specific hierarchy is up to you,
		// but it must be in a JSON format and should match the load function.
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		BookCollection bookCollection = new BookCollection(books);               //这里要new一个实例
		try (FileWriter fw = new FileWriter(file)) {
			gson.toJson(bookCollection, fw);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * Saves this collection to the specified XML file.
	 *
	 * @param file The path to a file.
	 */
	public void saveToXMLFile(File file) {
		// TODO: Implement this function yourself. The specific hierarchy is up to you,
		// but it must be in an XML format and should match the
		// load function.

		//Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents. (see doc)
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element rootElem = doc.createElement("books");
			doc.appendChild(rootElem);
			for (Book book: books){
				Element bookElem = doc.createElement("book");
				rootElem.appendChild(bookElem);

				Element title = doc.createElement("title");
				title.appendChild(doc.createTextNode(book.title));
				bookElem.appendChild(title);

				Element authorName = doc.createElement("authorName");
				authorName.appendChild(doc.createTextNode(book.authorName));
				bookElem.appendChild(authorName);

				Element yearReleased = doc.createElement("yearReleased");
				yearReleased.appendChild(doc.createTextNode(Integer.toString(book.yearReleased)));
				bookElem.appendChild(yearReleased);

				Element bookGenre = doc.createElement("bookGenre");
				bookGenre.appendChild(doc.createTextNode(book.bookGenre.toString()));       //注意这里另外一个类型的调用
				bookElem.appendChild(bookGenre);

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING,"utf-8");
				transformer.setOutputProperty(OutputKeys.INDENT,"yes");

				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(file);
				transformer.transform(source,result);

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Load a pre-existing book collection from a "bespoke" file.
	 *
	 * @param file The file to load from. This is guaranteed to exist.
	 * @return An initialised book collection.
	 */
	public static BookCollection loadFromBespokeFile(File file) {
		// TODO: Implement this function yourself.
		List<Book> bookList = new ArrayList<>();
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] books = line.split("\n");            //一排一排读 存的时候按排存的
				for (int i = 0; i < books.length; i++) {
					books[i].trim(); // trim 可以去除字符串头尾的空格
					String elems[] = books[i].split(";"); // 创造的时候是按照“；”来分的，所以现在也用；来区分。
					bookList.add(new Book(elems[0], elems[1], Integer.parseInt(elems[2]), get_book_Genre(elems[3])));    //种类在下面获得
				}// elem[0] title, elems[1]: authorName,elems[2] yearReleased, elems[3] bookGenre
				// 因为原来是string， 要将string转换为对应的type，所以用get_book_genre 来将string转换为gerne
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BookCollection(bookList);
	}


	public static BookGenre get_book_Genre(String a) {    // 因为原来是string， 要将string转换为对应的type，所以用get_book_genre 来将string转换为gerne
		switch (a) {
			case "FICTION_ACTION":
				return BookGenre.FICTION_ACTION;
			case "FICTION_COMEDY":
				return BookGenre.FICTION_COMEDY;
			case "FICTION_FANTASY":
				return BookGenre.FICTION_FANTASY;
			case "NON_FICTION":
				return BookGenre.NON_FICTION;
		}
		return null;
	}

	/**
	 * Load a pre-existing book collection from a JSON file.
	 *
	 * @param file The file to load from. This is guaranteed to exist.
	 * @return An initialised book collection.
	 */
	public static BookCollection loadFromJSONFile(File file) {
		// TODO: Implement this function yourself.
		Gson gson = new Gson();
		JsonReader jsonReader = null;

		final Type BookCollection_TYPE = new TypeToken<BookCollection>() {
		}.getType();
		//or TypeToken.getParameterized(ArrayList.class, PersonJSON.class).getType();

		try {
			jsonReader = new JsonReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return gson.fromJson(jsonReader, BookCollection_TYPE);

	}

	/**
	 * Load a pre-existing book collection from an XML file.
	 *
	 * @param file The file to load from. This is guaranteed to exist.
	 * @return An initialised book collection.
	 */
	public static BookCollection loadFromXMLFile(File file) {
		// TODO: Implement this function yourself.

		List<Book> bookList = new ArrayList<>();
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			NodeList nl = doc.getElementsByTagName("book");
			for (int i=0;i<nl.getLength();i++){
				Node n = nl.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE){
					Element elm = (Element)n;
					String title = elm.getElementsByTagName("title").item(0).getTextContent();
					String authorName = elm.getElementsByTagName("authorName").item(0).getTextContent();
					int yearReleased = Integer.parseInt(elm.getElementsByTagName("yearReleased").item(0).getTextContent());
					String bookGenre = elm.getElementsByTagName("bookGenre").item(0).getTextContent();
					bookList.add(new Book(title,authorName, yearReleased, get_book_Genre(bookGenre)));
				}}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BookCollection(bookList);
	}
}
