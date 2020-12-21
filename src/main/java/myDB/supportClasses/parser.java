package myDB.supportClasses;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class parser {

    public static ArrayList<dataNode> parseXML(String path, DateTimeFormatter formatter) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(path);
        ArrayList<dataNode> list = new ArrayList<>();
        Node root = document.getDocumentElement();
        NodeList subjects = root.getChildNodes();
        for (int i = 0; i < subjects.getLength(); i++) {
            dataNode dataNode = new dataNode();
            ArrayList<lesson> lessons = new ArrayList<>();
            NodeList subAtr = subjects.item(i).getChildNodes();
            for (int j = 0; j < subAtr.getLength(); j++) {
                if (j == 1) {
                    dataNode.setSubjectName(subAtr.item(j).getTextContent()); //Subject Name
                } else if (j == 3) {
                    dataNode.setTeacherId(subAtr.item(j).getTextContent()); // Teacher Name
                } else if (j == 5) {
                    dataNode.setGroupId(Integer.parseInt(subAtr.item(j).getTextContent())); // Date
                } else if (subAtr.item(j).getNodeType() != Node.TEXT_NODE) { //start to set Students
                    NodeList studentsList = subAtr.item(j).getChildNodes();
                    for (int k = 0; k < studentsList.getLength(); k++) {
                        NodeList stud = studentsList.item(k).getChildNodes();
                        String student = null;
                        String comment = null;
                        int mark = 0;
                        lesson les = new lesson();
                        for (int l = 0; l < stud.getLength(); l++) {
                            if (l == 1) {
                                les.setName(stud.item(l).getTextContent().trim());
                            } else if (l == 3) {
                                les.setDate(LocalDate.parse(stud.item(l).getTextContent().trim(), formatter));
                            } else if (stud.item(l).getNodeType() != Node.TEXT_NODE) {
                                NodeList curStudL = stud.item(l).getChildNodes();
                                for (int m = 0; m < curStudL.getLength(); m++) {
                                    NodeList newList = curStudL.item(m).getChildNodes();
                                    for (int o = 0; o < newList.getLength(); o++) {
                                        if (o == 1) {
                                            student = newList.item(o).getTextContent();
                                        } else if (o == 3) {
                                            mark = Integer.parseInt(newList.item(o).getTextContent());
                                        } else if (o == 5) {
                                            comment = newList.item(o).getTextContent();
                                            les.addNodeToMap(student, mark, comment);
                                        }

                                    }
                                }
                                lessons.add(les);
                            }
                        }
                    }
                    dataNode.setLessons(lessons);
                }

            }
            if(dataNode.getSubjectName() != null)
            list.add(dataNode);
        }
        return list;
    }

}
