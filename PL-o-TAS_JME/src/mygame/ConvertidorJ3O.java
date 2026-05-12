import com.jme3.app.SimpleApplication;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;

import java.io.File;

public class ConvertidorJ3O extends SimpleApplication {

    public static void main(String[] args) {
        ConvertidorJ3O app = new ConvertidorJ3O();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {

        /*try {

            // Cargar modelo
            Spatial modelo = assetManager.loadModel("");

            // Exportador
            BinaryExporter exporter = BinaryExporter.getInstance();

            // Guardar .j3o
            File archivoSalida = new File("assets/Models/enemigo.j3o");

            exporter.save(modelo, archivoSalida);

            System.out.println("Modelo convertido a J3O correctamente ⚔️");

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println(assetManager.loadModel(""));
        stop();
    }
}