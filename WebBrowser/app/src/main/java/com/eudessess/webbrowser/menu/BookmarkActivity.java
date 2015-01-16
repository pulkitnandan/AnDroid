package com.eudessess.webbrowser.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eudessess.webbrowser.R;
import com.eudessess.webbrowser.database.InbuiltDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BookmarkActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    String classes[] = new String[100];
    List<String> book = new ArrayList<String>();
    List<String> urlList = new ArrayList<String>();
    Button bSend, bRetrieve, bLogin;
    String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        InbuiltDatabase bdb = new InbuiltDatabase(this, "BOOKMARK");
        Cursor c = bdb.fetchData();
        try {
            if (c != null) {
                if (c.moveToFirst()) {
                    str += "<bookmark>";
                    do {
                        String title = c.getString(c.getColumnIndex("title"));
                        String url = c.getString(c.getColumnIndex("url"));
                        book.add(title + "\n" + url);
                        urlList.add(url);
                        str += "<item><title>" + title + "</title><url>" + url
                                + "</url></item>";
                    } while (c.moveToNext());
                    str += "</bookmark>";
                    ListView books = (ListView) findViewById(R.id.listView1);
                    books.setOnItemClickListener(this);
                    books.setOnItemLongClickListener(this);
                    books.setAdapter(new ArrayAdapter<String>(BookmarkActivity.this,
                            android.R.layout.simple_list_item_1, book));
                }
            } else {
                Toast.makeText(this, "There are no Bookmarks to show...",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open bookmarks database...",
                    Toast.LENGTH_SHORT).show();
        }
        bdb.close();
        c.close();
        bSend = (Button) findViewById(R.id.bSend);
        bRetrieve = (Button) findViewById(R.id.bRetrieve);
        bSend.setOnClickListener(this);
        bRetrieve.setOnClickListener(this);
    }

    EditText etUser, etPass, Ruser;
    AlertDialog ad, ad1;

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bSend:
	            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                alert.setTitle("Login for the bookmark...");
                final TextView t1 = new TextView(this);
                etUser = new EditText(this);
                final TextView t2 = new TextView(this);
                etPass = new EditText(this);
                etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                t1.setText("Username");
                t2.setText("Password");
                final LinearLayout layouthori = new LinearLayout(this);
                layouthori.setOrientation(LinearLayout.HORIZONTAL);
                final Button ok = new Button(this);
                final Button cancel = new Button(this);
                ok.setText("Login");
                cancel.setText("Cancel");
                ok.setOnClickListener(this);
                cancel.setOnClickListener(this);

                layout.addView(t1);
                layout.addView(etUser);
                layout.addView(t2);
                layout.addView(etPass);
                layouthori.addView(ok);
                layouthori.addView(cancel);
                layout.addView(layouthori);
                alert.setView(layout);
                ok.setId(R.id.bSend);
                cancel.setId(R.id.cancel1);
                ad = alert.create();
                ad.show();

                break;

            case R.id.bRetrieve:
                try {
                    final AlertDialog.Builder alrt = new AlertDialog.Builder(this);
                    final LinearLayout lay = new LinearLayout(this);
                    lay.setOrientation(LinearLayout.VERTICAL);
                    alrt.setTitle("Login for the bookmark...");
                    final TextView username = new TextView(this);
                    Ruser = new EditText(this);

                    username.setText("Username");
                    final LinearLayout layouthor = new LinearLayout(this);
                    layouthor.setOrientation(LinearLayout.HORIZONTAL);
                    final Button ok1 = new Button(this);
                    final Button cancel1 = new Button(this);
                    ok1.setText("Retrieve");
                    cancel1.setText("Cancel");
                    ok1.setOnClickListener(this);
                    cancel1.setOnClickListener(this);

                    lay.addView(username);
                    lay.addView(Ruser);
                    layouthor.addView(ok1);
                    layouthor.addView(cancel1);
                    lay.addView(layouthor);
                    alrt.setView(lay);
                    ok1.setId(R.id.bSend);
                     cancel1.setId(0);
                    ad1 = alrt.create();
                    ad1.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.string.bLogin:
                WebView webView = new WebView(this);

                setContentView(webView);

                String url = "TO BE FILLED WITH URL";
                String postData = "bookmarks=" + str + "&username="
                        + etUser.getText().toString() + "&password="
                        + etPass.getText().toString();
                webView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
                ad.dismiss();
                break;

            case R.string.cancel:
                ad.dismiss();
                break;
            case R.string.bRetrieve:
                try {
                    executeHttpGet(Ruser.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ad1.dismiss();
                break;

            case R.string.bRCancel:
                ad1.dismiss();
                break;
        }
    }

    public void executeHttpGet(String user) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(
                    "TO BE FILLED WITH URL" + user));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
            try {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(
                        new ByteArrayInputStream(page.getBytes("utf-8"))));
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("item");
                String sd = "";
                int x = 1;
                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        String tt = getTagValue("title", eElement).toString();
                        String ur = getTagValue("url", eElement).toString();
                        InbuiltDatabase bdb = new InbuiltDatabase(this, "BOOKMARK");

                        try {
                            bdb.insertRow(tt, ur);

                        } catch (Exception ex) {
                            String problem = "Bookmark Adding failed" + "\n"
                                    + ex.getMessage();
                            Toast.makeText(this, problem, Toast.LENGTH_SHORT);
                        }
                        sd += x + ".) " + tt + "  " + ur + "\n";
                        x++;
                    }
                }
                Toast.makeText(this, "Bookmarks have been added!!!",
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(this, sd, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            Class classSelect = Class.forName("com.eudessess.webbrowser.menu.browseractivity");
            Intent disp = new Intent(BookmarkActivity.this, classSelect);
            String ur = urlList.get(arg2);
            Bundle ans = new Bundle();
            ans.putString("urlSelected", ur);
            disp.putExtras(ans);
            startActivity(disp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        try {
            String ur;
            ur = urlList.get(arg2);
            InbuiltDatabase bdb = new InbuiltDatabase(this, "BOOKMARK");
            bdb.deleteData(ur);
            bdb.close();
            startActivity(getIntent());
            Toast.makeText(this, "Bookmark Deleted: \n" + book.get(arg2),
                    Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
