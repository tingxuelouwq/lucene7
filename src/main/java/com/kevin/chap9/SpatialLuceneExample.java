package com.kevin.chap9;

import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名: SpatialLuceneExample<br/>
 * 包名：com.kevin.chap9<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/25 10:11<br/>
 * 版本：1.0<br/>
 * 描述：Lucene Spatial测试用例<br/>
 */
public class SpatialLuceneExample {

    /**
     * Spatial4j上下文
     * 1：SpatialContext初始化可由SpatialContextFactory配置
     * 2：SpatialContext属性
     *          DistanceCalculator(默认使用GeodesicSphereDistCalc.Haversine,将地球视为标准球体)
     *          ShapeFactory(默认使用ShapeFactoryImpl)
     *          Rectangle(构建经纬度空间:RectangleImpl(-180, 180, -90, 90, this))
     *          BinaryCodec()
     */
    private SpatialContext ctx;

    /**
     * 索引和查询模型的策略接口
     */
    private SpatialStrategy strategy;

    private Directory directory;

    private void init() throws IOException {
        /**
         * SpatialContext也可以通过SpatialContextFactory工厂类来构建
         */
        this.ctx = SpatialContext.GEO;

        /**
         * 网格最大11层或Geo Hash的精度
         * 1：SpatialPrefixTree定义的Geo Hash最大精度为24
         * 2：GeohashUtils定义类经纬度到Geo Hash值公用方法
         */
        SpatialPrefixTree spatialPrefixTree = new GeohashPrefixTree(ctx, 11);

        /**
         * 索引和搜索的策略接口，两个主要实现类
         * 1：RecursivePrefixTreeStrategy(支持任何Shape的索引和搜索)
         * 2：TermQueryPrefixTreeStrategy(仅支持Point Shape)
         * 上述两个类继承PrefixTreeStrategy
         */
        this.strategy = new RecursivePrefixTreeStrategy(spatialPrefixTree,
                "location");

        this.directory = FSDirectory.open(Paths.get("D:/lucene/index"));
    }

    private void createIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig()
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        List<CityGeoInfo> cityGeoInfos = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass()
                .getClassLoader().getResourceAsStream("geo/city.txt")));
        String line;
        while ((line = br.readLine()) != null) {
            String[] info = line.split(":");
            cityGeoInfos.add(new CityGeoInfo(Long.parseLong(info[0]), info[1],
                    Double.parseDouble(info[2]), Double.parseDouble(info[3])));
        }

        List<Document> documents = createDocuments(ctx, strategy, cityGeoInfos);
        indexWriter.addDocuments(documents);
        indexWriter.close();
    }

    private List<Document> createDocuments(SpatialContext ctx,
                                           SpatialStrategy strategy,
                                           List<CityGeoInfo> cityGeoInfos) {
        List<Document> docs = new ArrayList<>();
        for (CityGeoInfo cityGeoInfo : cityGeoInfos) {
            Document doc = new Document();
            doc.add(new StoredField("id", cityGeoInfo.getId()));
            doc.add(new NumericDocValuesField("id", cityGeoInfo.getId()));
            doc.add(new StringField("city", cityGeoInfo.getName(), Field.Store.YES));

            Point point = ctx.getShapeFactory().pointXY(cityGeoInfo.getLongitude(),
                    cityGeoInfo.getLatitude());
            Field[] fields = strategy.createIndexableFields(point);
            for (Field field : fields) {
                doc.add(field);
            }
            doc.add(new StoredField(strategy.getFieldName(),
                    point.getX() + "," + point.getY()));
            docs.add(doc);
        }
        return docs;
    }

    private void search() throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        // 按照id升序排序
        Sort idSort = new Sort(new SortField("id", SortField.Type.LONG));
        // 搜索方圆100千米范围以内，以当前位置经纬度(120.33,36.07)青岛为圆心
        SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
                ctx.getShapeFactory().circle(120.33, 36.07, DistanceUtils
                        .dist2Degrees(100, DistanceUtils.EARTH_MEAN_RADIUS_KM)));
        Query query = strategy.makeQuery(args);
        TopDocs topDocs = searcher.search(query, 10, idSort);
        printDocument(topDocs, searcher, args.getShape().getCenter());

        System.out.println("==========================================================");

        // 定义坐标点(x,y)，即(经度,纬度)，即当前用户所在地点(烟台)
        Point point = ctx.getShapeFactory().pointXY(121.39, 37.52);
        /**
         * 计算当前用户所在坐标点与索引坐标点中心之间的距离，即计算当前用户所在坐标点与每个
         * 待匹配地点之间的距离，DEG_TO_KM表示以KM为党委
         */
        DoubleValuesSource valueSource = strategy.makeDistanceValueSource(point,
                DistanceUtils.DEG_TO_KM);
        /**
         * 根据名重点与当前位置坐标点的距离远近降序排序
         */
        Sort distSort = new Sort(valueSource.getSortField(false).rewrite(searcher));
        topDocs = searcher.search(new MatchAllDocsQuery(), 10, distSort);
        printDocument(topDocs, searcher, point);

        reader.close();
    }

    private void printDocument(TopDocs topDocs, IndexSearcher searcher, Point center)
            throws IOException {
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = searcher.doc(docId);
            int cityId = document.getField("id").numericValue().intValue();
            String city = document.getField("city").stringValue();
            String location = document.getField(strategy.getFieldName()).stringValue();
            String[] locations = location.split(",");
            double xPoint = Double.parseDouble(locations[0]);
            double yPoint = Double.parseDouble(locations[1]);
            double distDEG = ctx.calcDistance(center, xPoint, yPoint);
            double dist = DistanceUtils.degrees2Dist(distDEG, DistanceUtils.EARTH_MEAN_RADIUS_KM);
            System.out.println("docId=" + docId + "\tcityId=" + cityId +
                    "\tcity=" + city + "\tdistance=" + dist + "KM");
        }
    }

    public static void main(String[] args) throws IOException {
        SpatialLuceneExample example = new SpatialLuceneExample();
        example.init();
        example.createIndex();
        example.search();
    }
}
