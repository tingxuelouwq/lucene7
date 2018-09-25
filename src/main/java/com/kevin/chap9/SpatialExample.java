package com.kevin.chap9;

import com.kevin.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * 类名: SpatialExample<br/>
 * 包名：com.kevin.chap9<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/25 16:13<br/>
 * 版本：1.0<br/>
 * 描述：Lucene Spatial官方测试用例<br/>
 */
public class SpatialExample {

    /** Spatial4j上下文 */
    private SpatialContext ctx;

    /** 提供索引和查询模型的策略接口 */
    private SpatialStrategy strategy;

    /** 索引目录 */
    private Directory directory;

    private void init() throws IOException {
        // SpatialContext也可以通过SpatialContextFactory工厂类来构建
        this.ctx = SpatialContext.GEO;

        // 网格最大11层
        int maxLevels = 11;

        // Spatial Tiers
        SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);

        this.strategy = new RecursivePrefixTreeStrategy(grid, "myGeoField");
        this.directory = FSDirectory.open(Paths.get("D:/lucene/index"));
    }

    private void indexPoints() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(new SmartChineseAnalyzer())
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        File file = new File("D:\\Workspace\\Idea\\lucene7\\src\\main\\resources\\geo\\shop.json");
        String content = FileUtils.readFileToString(file, "UTF-8");
        List<Shop> shops = JsonUtil.jsonArray2List(content, Shop.class);
        for (Shop shop : shops) {
            Document doc = newSampleDoucment(shop.getId(), shop.getName(), ctx
                    .getShapeFactory().pointXY(shop.getLongitude(), shop.getLatitude()));
            writer.addDocument(doc);
        }

        writer.close();
    }

    private Document newSampleDoucment(int id, String title, Shape... shapes) {
        Document doc = new Document();
        doc.add(new StoredField("id", id));
        doc.add(new NumericDocValuesField("id", id));
        doc.add(new TextField("name", title, Field.Store.YES));

        // Potentially more than one shape in this field is supported by some
        // strategies; see the javadocs of the SpatialStrategy impl to see.
        for (Shape shape : shapes) {
            for (Field f : strategy.createIndexableFields(shape)) {
                doc.add(f);
            }
            // store it too; the format is up to you
            // (assume point in this example)
            Point pt = (Point) shape;
            doc.add(new StoredField(strategy.getFieldName(),
                    pt.getX() + " " + pt.getY()));
        }

        return doc;
    }

    private void search(String keyword) throws IOException {
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // --Filter by circle (<= distance from a point)
        // Search with circle
        // note: SpatialArgs can be parsed from a string
        Point pt = ctx.getShapeFactory().pointXY(121.41791, 31.21867);
        // the distance in km
        DoubleValuesSource valuesSource =
                strategy.makeDistanceValueSource(pt, DistanceUtils.DEG_TO_KM);
        // 按距离由近及远排序
        Sort distSort = new Sort(valuesSource.getSortField(false))
                .rewrite(searcher); // false=ascist

        SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects,
                ctx.getShapeFactory().circle(pt, DistanceUtils.dist2Degrees(3.0,
                        DistanceUtils.EARTH_MEAN_RADIUS_KM)));
        Query query = strategy.makeQuery(args);

        BooleanQuery.Builder bqb = new BooleanQuery.Builder();
        bqb.add(query, BooleanClause.Occur.MUST);
        bqb.add(new TermQuery(new Term("name", keyword)), BooleanClause.Occur.MUST);

        TopDocs docs = searcher.search(bqb.build(), 20, distSort);
        printDocs(searcher, docs, args);

        reader.close();
    }

    private void printDocs(IndexSearcher searcher, TopDocs docs, SpatialArgs args)
            throws IOException {
        for (int i = 0; i < docs.totalHits; i++) {
            Document doc = searcher.doc(docs.scoreDocs[i].doc);
            System.out.print(doc.getField("id").numericValue().intValue());
            System.out.print(":" + doc.getField("name").stringValue());

            //计算距离
            String docStr = doc.getField(strategy.getFieldName()).stringValue();
            // assume docStr is "x,y" as written in newSampleDocument()
            String[] xy = docStr.split(" ");
            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);
            double distDEG = ctx.calcDistance(args.getShape().getCenter(), x, y);
            double dist = DistanceUtils.degrees2Dist(distDEG,
                    DistanceUtils.EARTH_MEAN_RADIUS_KM);
            System.out.print("(" + dist + "km)");
            System.out.println();
        }
    }

    @Test
    public void test() throws IOException {
        init();
        indexPoints();
        search("密室");
        System.out.println("-----------------------------------");
        search("咖啡");
    }
}
