package fr.mrmicky.worldeditselectionvisualizer.compat.v6;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.FaweAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.Vectors6;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegionAdapter6 implements RegionAdapter {

    @NotNull
    private final Region region;

    public RegionAdapter6(@NotNull Region region) {
        this.region = Objects.requireNonNull(region, "region");
    }

    @NotNull
    @Override
    public Vector3d getMinimumPoint() {
        return Vectors6.toVector3d(region.getMinimumPoint());
    }

    @NotNull
    @Override
    public Vector3d getMaximumPoint() {
        return Vectors6.toVector3d(region.getMaximumPoint());
    }

    @NotNull
    @Override
    public Vector3d getCenter() {
        return Vectors6.toVector3d(region.getCenter());
    }

    @Override
    public List<Vector3d> getPolygonalPoints() {
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polygonalRegion = (Polygonal2DRegion) region;

            return polygonalRegion.getPoints().stream()
                    .map(vec -> new Vector3d(vec.getX(), 0, vec.getZ()))
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Vector3d getEllipsoidRadius() {
        if (region instanceof EllipsoidRegion) {
            return Vectors6.toVector3d(((EllipsoidRegion) region).getRadius());
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<Vector3d[]> getConvexTriangles(boolean faweSupport) {
        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion polygonalRegion = (ConvexPolyhedralRegion) region;

            return polygonalRegion.getTriangles().stream()
                    .map(this::triangleToVectors)
                    .collect(Collectors.toList());
        }

        if (faweSupport) {
            return FaweAdapter6.getConvexTriangles(region);
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void shift(Vector3d vector) throws RegionOperationException {
        region.shift(Vectors6.toVector(vector));
    }

    @NotNull
    @Override
    public Region transform(Transform transform) {
        if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector pos1 = transform.apply(cuboidRegion.getPos1());
            Vector pos2 = transform.apply(cuboidRegion.getPos2());

            return new CuboidRegion(region.getWorld(), pos1, pos2);
        }

        return region.clone();
    }

    @NotNull
    @Override
    public Region getRegion() {
        return region;
    }

    @NotNull
    private Vector3d[] triangleToVectors(Triangle triangle) {
        Vector3d[] vectors = new Vector3d[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors6.toVector3d(triangle.getVertex(i));
        }

        return vectors;
    }
}
