package viper;

import viper.wire.Bar;
import viper.wire.Benchmark;
import viper.wire.Foo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class Main {
    static final int count = 500_000;

    public static void main(String[] args) {
        new Main().main_1();
        new Main().main_2();
    }

    void main_1() {

        // create a big object
        var bars = IntStream.range(0,count).mapToObj(this::wireBar).toList();
        var foo = new viper.wire.Foo(139202, bars);

        timer("wire........", () -> {
            return Foo.ADAPTER.encode(foo).length;
        });
    }

    void main_2() {

        // create a big object
        var bars = IntStream.range(0,count).mapToObj(this::pbBar).toList();
        var foo = Benchmark.Foo.newBuilder().setNumber(139202).addAllBars(bars).build();

        timer("protobuf....", () -> {
            try {
                var baos = new ByteArrayOutputStream();
                foo.writeTo(baos);
                return baos.size();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    Bar wireBar(int i) {
        return new Bar.Builder()
            .name("" + i)
            .age((i*2)+14)
            //.zip(84009)
            //.city(Integer.valueOf(i).hashCode() + ":::")
            //.num(99999)
            .build();
    }

    Benchmark.Bar pbBar(int i) {
        return Benchmark.Bar.newBuilder()
            .setName("" + i)
            .setAge( (i*2)+14)
            //.setZip(84009)
            //.setCity(Integer.valueOf(i).hashCode() + ":::")
            //.setNum(99999)
            .build();
    }


    void timer(String name, IntSupplier rn) {
        long start = System.nanoTime();
        long size = rn.getAsInt();
        long elapsed = System.nanoTime() - start;
        System.out.println(name + " " + (elapsed/1000000) + "ms  (" + size + "B)");
    }
}
