package example.movies.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static spark.Spark.get;

public class MovieRoutes implements SparkApplication {

    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private MovieService service;

    public MovieRoutes(MovieService service) {
        this.service = service;
    }

    public void init() {
        get("/movie/:title", (req, res) -> gson.toJson(service.findMovie(URLDecoder.decode(req.params("title"),"utf-8"))));
        get("/search", (req, res) -> gson.toJson(service.search(req.queryParams("q"))));
        get("/graph", (req, res) -> {
            Map<String,Object> queryMap = getMapParams(req.queryString());
            int limit = queryMap.containsKey("limit") ? Integer.valueOf((String) queryMap.get("limit")) : 100;
            String entity = queryMap.containsKey("entity") ? (String) queryMap.get("entity") : "";
            return gson.toJson(service.graph(limit, entity));
        });
    }

    private Map<String,Object> getMapParams(String queryString){
        Map<String,Object> map = new HashMap<>();
        String[] split = queryString.contains(",") ? queryString.split(",") : new String[]{queryString};
        IntStream.range(0,split.length).forEach(i -> {
            String[] kv = split[i].split("=");
            map.put(kv[0],kv[1]);
        });
        return map;
    }
}
