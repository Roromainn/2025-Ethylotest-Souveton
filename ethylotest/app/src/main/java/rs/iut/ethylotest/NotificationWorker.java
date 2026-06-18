package rs.iut.ethylotest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

/** Worker exécuté en arrière-plan pour envoyer la notification "peut conduire". */
public class NotificationWorker extends Worker {

    /** Crée le worker avec le contexte et les paramètres fournis par WorkManager. */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(Constantes.PREFS_NAME, Context.MODE_PRIVATE);

        double taux = Double.parseDouble(prefs.getString(Constantes.PREF_TAUX, "0.0"));
        long timestamp = prefs.getLong(Constantes.PREF_TIMESTAMP, System.currentTimeMillis());
        double heuresEcoulees = (System.currentTimeMillis() - timestamp) / 3600000.0;
        taux = Math.max(0, taux - heuresEcoulees * Constantes.TAUX_ELIMINATION);

        boolean debutant = false;
        String strPersonne = prefs.getString(Constantes.PREF_PERSONNE, null);
        if (strPersonne != null) {
            Personne personne = new Gson().fromJson(strPersonne, Personne.class);
            debutant = personne.isDebutant();
        }
        double seuil = debutant ? Constantes.SEUIL_DEBUTANT : Constantes.SEUIL_NORMAL;

        // Vérifie que le taux est toujours sous le seuil (l'utilisateur a pu boire davantage)
        if (taux <= seuil) {
            envoyerNotification();
        }

        return Result.success();
    }

    private void envoyerNotification() {
        Context ctx = getApplicationContext();
        NotificationManager manager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    Constantes.NOTIF_CHANNEL_ID,
                    ctx.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, Constantes.NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(ctx.getString(R.string.notif_titre))
                .setContentText(ctx.getString(R.string.notif_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(Constantes.NOTIF_ID, builder.build());
    }
}
