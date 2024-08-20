package com.nz.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VWorldApiClientService {
    private static final String API_KEY = "F320E4DD-72C4-3869-9343-68E55DB54345"; // 여기에 VWorld API 키를 입력하세요
    
    public List<String> getSuggestions(String query) throws Exception {
        List<String> suggestions = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String urlStr = "https://api.vworld.kr/req/search?service=search&request=search&version=2.0&format=json&type=place&query="
                            + encodedQuery + "&key=" + API_KEY;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // 응답 로그 출력
                System.out.println("API 응답: " + response.toString());
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject responseObj = jsonResponse.getJSONObject("response");
                
                // status 체크
                if (!responseObj.getString("status").equals("OK")) {
                    throw new Exception("API 응답 오류: " + responseObj.getString("status"));
                }

                // result가 있는지 체크
                if (responseObj.has("result")) {
                    JSONObject result = responseObj.getJSONObject("result");
                    if (result.has("items")) {
                        JSONArray items = result.getJSONArray("items");
                        
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            String title = item.optString("title", "제목 없음");
                            suggestions.add(title);
                        }
                    }
                }
            } else {
                throw new Exception("HTTP error code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // 예외를 다시 던져 호출자에게 알림
        }
        return suggestions;
    }
    
    public double[] getCoordinates(String address) throws Exception {
        double[] coords = new double[2];
        String encodedAddress = URLEncoder.encode(address, "UTF-8");
        String urlStr = "https://api.vworld.kr/req/address?service=address&request=getCoord&version=2.0&crs=epsg:4326&address="
                        + encodedAddress + "&format=json&type=road&key=" + API_KEY;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 로그 출력
            System.out.println("API 응답: " + response.toString());

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject responseObj = jsonResponse.optJSONObject("response");
            if (responseObj != null) {
                String status = responseObj.optString("status");
                if ("OK".equals(status)) {
                    JSONObject result = responseObj.optJSONObject("result");
                    if (result != null) {
                        JSONObject point = result.optJSONObject("point");
                        if (point != null) {
                            coords[0] = point.getDouble("y");
                            coords[1] = point.getDouble("x");
                            return coords;
                        } else {
                            throw new Exception("API 응답에 좌표 정보가 없습니다.");
                        }
                    } else {
                        throw new Exception("API 응답에 결과 정보가 없습니다.");
                    }
                } else if ("NOT_FOUND".equals(status)) {
                    throw new Exception("주소를 찾을 수 없습니다.");
                } else {
                    throw new Exception("API 응답 오류: " + status);
                }
            } else {
                throw new Exception("잘못된 API 응답 형식입니다.");
            }
        } else {
            throw new Exception("HTTP error code: " + responseCode);
        }
    }
}
