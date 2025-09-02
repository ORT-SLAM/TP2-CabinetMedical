package sio.tp2;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import sio.tp2.Model.RendezVous;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class TP2Controller implements Initializable {

    private TreeMap<String, TreeMap<String, RendezVous>> monPlanning;

    @FXML
    private TextField txtNomPatient;
    @FXML
    private ComboBox<String> cboNomPathologie;
    @FXML
    private TreeView<String> tvPlanning;
    @FXML
    private DatePicker dpDateRdv;
    @FXML
    private Spinner<Integer> spHeure;
    @FXML
    private Spinner<Integer> spMinute;
    @FXML
    private Button cmdValider;

    @FXML
    public void cmdValiderClicked(Event event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur sur vos informations");
        alert.setHeaderText("");

        String nomPatient = txtNomPatient.getText();

        if (nomPatient.isEmpty()) {
            alert.setContentText("Veuillez entrer votre nom");
            alert.showAndWait();
            return;
        }
        if (dpDateRdv.getValue() == null) {
            alert.setContentText("Veuillez entrer une date");
            alert.showAndWait();
            return;
        }

        String heureRdv = String.format("%02d:%02d", spHeure.getValue(), spMinute.getValue());
        String nomPathologie = cboNomPathologie.getValue();
        RendezVous rendezVous = new RendezVous(heureRdv, nomPatient, nomPathologie);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateKey = dpDateRdv.getValue().format(formatter);

        if (!monPlanning.containsKey(dateKey)) {
            monPlanning.put(dateKey, new TreeMap<>());
        }

        if (monPlanning.get(dateKey).containsKey(heureRdv)) {
            alert.setContentText("Il y a déjà un rendez-vous à cette heure");
            alert.showAndWait();
            return;
        }

        monPlanning.get(dateKey).put(heureRdv, rendezVous);

        clearForm();
        updateTreeView();
    }

    private void clearForm() {
        txtNomPatient.clear();
        cboNomPathologie.getSelectionModel().selectFirst();
        dpDateRdv.setValue(null);
    }

    private void updateTreeView() {
        TreeItem<String> root = new TreeItem<>("Mon planning");

        monPlanning.forEach((date, rdvs) -> {
            TreeItem<String> dateNode = new TreeItem<>(date);

            rdvs.forEach((heure, rv) -> {
                TreeItem<String> heureNode = new TreeItem<>(rv.getHeureRdv());
                TreeItem<String> patientNode = new TreeItem<>("Patient : " + rv.getNomPatient());
                TreeItem<String> pathologieNode = new TreeItem<>("Pathologie : " + rv.getNomPathologie());

                heureNode.getChildren().add(patientNode);
                heureNode.getChildren().add(pathologieNode);
                dateNode.getChildren().add(heureNode);
                heureNode.setExpanded(true);
            });

            root.getChildren().add(dateNode);
            dateNode.setExpanded(true);
        });

        tvPlanning.setRoot(root);
        root.setExpanded(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        monPlanning = new TreeMap<>();
        cboNomPathologie.getItems().addAll("Angine", "Grippe", "Covid", "Gastro");
        cboNomPathologie.getSelectionModel().selectFirst();
        SpinnerValueFactory<Integer> spinnerValueFactoryHeure =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 19, 8, 1);
        spHeure.setValueFactory(spinnerValueFactoryHeure);
        SpinnerValueFactory<Integer> spinnerValueFactoryMinute =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15);
        spMinute.setValueFactory(spinnerValueFactoryMinute);
    }
}
