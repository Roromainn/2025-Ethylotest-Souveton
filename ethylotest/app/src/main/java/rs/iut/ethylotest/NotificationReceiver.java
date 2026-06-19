package rs.iut.ethylotest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/** BroadcastReceiver déclenché par AlarmManager pour envoyer la notification "peut conduire". */
public class NotificationReceiver extends BroadcastReceiver {

    /** Vérifie le taux actuel et envoie la notification si la personne peut conduire. */
    @Override
    public void onReceive(Context context, Intent intent) {
        AlcoolRepository repository = new AlcoolRepository(context);
        double taux = AlcoolCalculateur.calculerTauxActuel(repository.loadTaux(), repository.loadTimestamp());
        Personne personne = repository.loadPersonne();
        double seuil = personne != null ? AlcoolCalculateur.getSeuil(personne) : Constantes.SEUIL_NORMAL;

        if (taux <= seuil) {
            envoyerNotification(context);
        }
    }

    private void envoyerNotification(Context context) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    Constantes.NOTIF_CHANNEL_ID,
                    context.getString(R.string.notif_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constantes.NOTIF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notif_titre))
                .setContentText(context.getString(R.string.notif_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(Constantes.NOTIF_ID, builder.build());
    }
}
